var workerThreads = require("worker_threads");
var commandLineInterface = require("../../command-line-interface");
const androidStudioLogging = require("../../script-helpers/android-studio-logging");

module.exports = function () {
    var pool = [], queue = [], finishListeners = {}, allJobs = {}, jobDependencyGraph = {};

    var workerCount = commandLineInterface.threads;

    //avoid the overhead of making a pool when only 1 thread is needed
    if(workerCount <= 1) return fakeWorkerPool();

    for (var i = workerCount; i >= 0; i--) pool.push(createWorkerWrap(queue, finishListeners, allJobs, jobDependencyGraph));

    function findOpenWorker() {
        for(var i = 0; i < pool.length; i++) {
            if(!pool[i].busy) return pool[i];
        }
    }

    return {
        giveJob: function (fileContext, cb) {
            var w = findOpenWorker();
            if(w) {
                w.assignJob(fileContext, cb);
            } else {
                queue.push({job:fileContext,cb:cb});
            }
        },
        close: function() {
            pool.forEach(x=>x.close());
        }
    }

}

function fakeWorkerPool() {
    var worker = require("./worker");
    return {
        giveJob: async function(fileContext, cb) {
            var fin = await worker(fileContext);
            cb(fin);
        },
        close: function() {

        }
    }
}

function createWorkerWrap(queue, finishListeners, allJobs, jobDependencyGraph) {
    var worker = new workerThreads.Worker(__dirname + "/worker.js", {
        workerData: process.argv.slice(2)
    });
    var callbacks = {}, jobNumber = 0;

    var wrap = {
        worker: worker,
        busy: false,
        assignJob: assignJob,
        close: close
    };

    function close() {
        worker.unref();
    }
    /**
     * 
     * @param {import("./transmutations").TransmutateContext} job 
     * @param {*} cb 
     */
    function assignJob(job, cb) {
        wrap.busy = true;
        const id = job.sourceFullFileName;
        callbacks[id] = cb;
        worker.postMessage({
            type: "newJob",
            body: job,
            id: id
        });
    }
    
    function assignFromQueue() {
        if (queue.length > 0) {
            var n = queue.shift();
            assignJob(n.job, n.cb);
        }
    }

    worker.on("message", function(m) {
        if(m.type == "jobDone") {
            if(callbacks[m.id]) callbacks[m.id](m.body);
            
            allJobs[job]
            callFinishListeners(finishListeners, m.id, m.body);

            wrap.busy = false;
            assignFromQueue();
        } else if(m.type == "jobWaitOnDependency") {
            const depId = m.dependencyId;
            const jobId = m.id;
            
            if(depId in allJobs) {
                worker.postMessage({
                    type: "dependencyComplete",
                    dependencyId: depId,
                    body: allJobs[depId]
                });
            } else {
                addFinishListener(finishListeners, depId, function (finishedContext) {
                    worker.postMessage({
                        type: "dependencyComplete",
                        dependencyId: depId,
                        body: finishedContext
                    });
                });
            }
            
            //noteJobDependency is true if it was successful (i.e. there was no circular dependencies)
            if(noteJobDependency(jobDependencyGraph, finishListeners, depId, jobId) == true) {
                wrap.busy = false;
                assignFromQueue();
            }
        }
    });

    return wrap;
}

function finishCircle(finishListeners, circle) {
    for(const jobId of circle) {
        callFinishListeners(finishListeners, jobId, undefined);
    }
}

function noteJobDependency(jobDependencyGraph, finishListeners, depId, jobId) {
    if (!(jobId in jobDependencyGraph)) jobDependencyGraph[jobId] = {};
    if (!(depId in jobDependencyGraph)) jobDependencyGraph[depId] = {};
    
    jobDependencyGraph[jobId][depId] = jobDependencyGraph[depId];
    
    const circle = checkForCircularDependencies(jobDependencyGraph, depId, jobId);
    if (circle !== undefined) finishCircle(finishListeners, circle);
    
    return circle === undefined;
}

function checkForCircularDependencies(jobDependencyGraph, checkFrom, original, path) {
    if(path === undefined) path = [original];
    
    for(const dependsOn in jobDependencyGraph[checkFrom]) {
        if(dependsOn == original) {
            androidStudioLogging.sendTreeLocationMessage({
                kind: "ERROR",
                text: "Circular dependency between files. Stopping compilation",
                original: "Dependency path:\n" + path.concat([dependsOn]).join("->\n")
            }, original);
            return path;
        }
        checkForCircularDependencies(jobDependencyGraph, dependsOn, original, path.concat([dependsOn]));
    }
    return undefined;
}

function callFinishListeners(finishListeners, jobId, finishedContext) {
    if(jobId in finishListeners) {
        const listeners = finishListeners[jobId];
        while(listeners.length > 0) {
            listeners.shift()(finishedContext)
        }
    }
}

function addFinishListener(finishListeners, jobId, callback) {
    if(!(jobId in finishListeners)) finishListeners[jobId] = [];
    finishListeners[jobId].push(callback);
}