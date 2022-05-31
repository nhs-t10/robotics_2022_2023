var workerThreads = require("worker_threads");
var commandLineInterface = require("../../command-line-interface");
const androidStudioLogging = require("../../script-helpers/android-studio-logging");

module.exports = function () {
    var pool = [], queue = [], finishListeners = {}, allJobs = {}, jobDependencyGraph = {};

    var workerCount = commandLineInterface.threads;

    //avoid the overhead of making a pool when only 1 thread is needed
    if(workerCount <= 1) {
        pool.push(fakeWorker(queue, finishListeners, allJobs, jobDependencyGraph));
    } else {
        for (var i = workerCount; i >= 0; i--) pool.push(initWorker(queue, finishListeners, allJobs, jobDependencyGraph));
    }

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
        addFinishedJobFromCache: function(fileContext) {
            const id = fileContext.sourceFullFileName;
            allJobs[id] = fileContext;
            callFinishListeners(finishListeners, id, fileContext);
        },
        finishGivingJobs: function() {
            clearNonExistantFinishListeners(allJobs, finishListeners);
        },
        close: function() {
            pool.forEach(x=>x.close());
        }
    }

}

function fakeWorker(queue, finishListeners, allJobs, jobDependencyGraph) {
    var worker = require("./worker");
    
    return createWorkerWrap(worker, queue, finishListeners, allJobs, jobDependencyGraph);
}

function initWorker(queue, finishListeners, allJobs, jobDependencyGraph) {
    var worker = new workerThreads.Worker(__dirname + "/worker.js", {
        workerData: process.argv.slice(2)
    });
    
    return createWorkerWrap(worker, queue, finishListeners, allJobs, jobDependencyGraph);
}

function createWorkerWrap(worker, queue, finishListeners, allJobs, jobDependencyGraph) {
    var callbacks = {};

    var wrap = {
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
        allJobs[id] = null;
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
            
            console.log("done1 ", m.id);
            
            allJobs[m.id] = m.body;
            callFinishListeners(finishListeners, m.id, m.body);

            wrap.busy = false;
            assignFromQueue();
        } else if(m.type == "jobWaitOnDependency") {
            const depId = m.dependencyId;
            const jobId = m.id;
            
            console.log("waiting on dep", depId);
            
            if(depId in allJobs) {
                worker.postMessage({
                    type: "dependencyComplete",
                    dependencyId: depId,
                    body: allJobs[depId]
                });
            } else {
                wrap.busy = false;
                addFinishListener(finishListeners, depId, function (finishedContext) {
                    wrap.busy = true;
                    console.log("finish dep :)");
                    worker.postMessage({
                        type: "dependencyComplete",
                        dependencyId: depId,
                        body: finishedContext
                    });
                });
            }
            
            //noteJobDependency is true if it was successful (i.e. there was no circular dependencies)
            if(noteJobDependency(jobDependencyGraph, finishListeners, depId, jobId) == true) {
                assignFromQueue();
            }
        }
    });

    return wrap;
}

function clearNonExistantFinishListeners(allJobs, finishListeners) {
    for(const jobId in finishListeners) {
        if(!(jobId in allJobs)) {
            callFinishListeners(finishListeners, jobId, null);
        }
    }
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