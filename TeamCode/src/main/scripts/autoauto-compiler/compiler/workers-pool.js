"use strict";

var workerThreads = require("worker_threads");
var commandLineInterface = require("../../command-line-interface");
const androidStudioLogging = require("../../script-helpers/android-studio-logging");


/**
 * @typedef {object} job
 * @property {TransmutateContext} fileContext
 * @property {jobCallback} cb
 */

/**
 * @callback jobCallback
 * @param {import("./worker.js").MaybeCompilation}
 */

/**
 * @typedef {Object.<string, finishListener[]>} t_finishListeners
 */

/**
 * @typedef {Object.<string, import("./worker.js").MaybeCompilation>} t_allJobs
 */

/**
 * @typedef {job[]} t_queue
 */

/**
 * @callback finishListener
 * @param {import("./worker.js").MaybeCompilation} finishedContext
 */

/**
 * @typedef {Object.<string, jobDependencyEntry>} t_jobDependencyGraph
 */

/**
 * @typedef {Object.<string, jobDependencyEntry>} jobDependencyEntry
 */


/**
 * @typedef {(import("./transmutations").TransmutateContext} TransmutateContext
 */

/**
 * @typedef {object} workerWrap
 * @property {boolean} busy
 * @property {(fileContext:TransmutateContext, job:jobCallback)=>void} assignJob
 * @property {()=>void} close
 * 
 */

module.exports = function () {
    var pool = [], queue = [], finishListeners = {}, allJobs = {}, jobDependencyGraph = {};

    var workerCount = commandLineInterface.threads;

    //avoid the overhead of making a pool when only 1 thread is needed
    if (workerCount <= 1) {
        pool.push(fakeWorker(queue, finishListeners, allJobs, jobDependencyGraph));
    } else {
        for (var i = workerCount; i >= 0; i--) pool.push(initWorker(queue, finishListeners, allJobs, jobDependencyGraph));
    }

    function findOpenWorker() {
        for (var i = 0; i < pool.length; i++) {
            if (!pool[i].busy) return pool[i];
        }
    }

    return {
        /**
         * 
         * @param {TransmutateContext} fileContext 
         * @param {jobCallback} cb 
         */
        giveJob: function (fileContext, cb) {
            var w = findOpenWorker();
            if (w) {
                w.assignJob(fileContext, cb);
            } else {
                queue.push({ fileContext: fileContext, cb: cb });
            }
        },
        addFinishedJobFromCache: function (fileContext) {
            const id = fileContext.sourceFullFileName;
            allJobs[id] = fileContext;
            callFinishListeners(finishListeners, id, {
                success: "SUCCESS",
                fileContext: fileContext,
                log: []
            });
        },
        finishGivingJobs: function () {
            clearNonExistantFinishListeners(allJobs, finishListeners);
        },
        close: function () {
            pool.forEach(x => x.close());
        }
    }

}



/**
 * 
 * @param {t_queue} queue 
 * @param {t_finishListeners} finishListeners 
 * @param {t_allJobs} allJobs 
 * @param {t_jobDependencyGraph} jobDependencyGraph 
 * @returns 
 */
function fakeWorker(queue, finishListeners, allJobs, jobDependencyGraph) {
    var worker = require("./worker");

    return createWorkerWrap(worker, queue, finishListeners, allJobs, jobDependencyGraph);
}

/**
 * 
 * @param {t_queue} queue 
 * @param {t_finishListeners} finishListeners 
 * @param {t_allJobs} allJobs 
 * @param {t_jobDependencyGraph} jobDependencyGraph 
 * @returns 
 */
function initWorker(queue, finishListeners, allJobs, jobDependencyGraph) {
    var worker = new workerThreads.Worker(__dirname + "/worker.js", {
        workerData: process.argv.slice(2)
    });

    return createWorkerWrap(worker, queue, finishListeners, allJobs, jobDependencyGraph);
}

/**
 * 
 * @param {Worker} worker 
 * @param {t_queue} queue 
 * @param {t_finishListeners} finishListeners 
 * @param {t_allJobs} allJobs 
 * @param {t_jobDependencyGraph} jobDependencyGraph
 * @returns {workerWrap}
 */
function createWorkerWrap(worker, queue, finishListeners, allJobs, jobDependencyGraph) {
    /** @type {Object.<string, jobCallback>} */
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
     * @param {TransmutateContext} job 
     * @param {jobCallback} cb 
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
            assignJob(n.fileContext, n.cb);
        }
    }

    worker.on("message", function (/** @type {import("./worker.js").InterThreadMessage} */m) {
        if (m.type == "jobDone") {
            if (callbacks[m.id]) callbacks[m.id](m.body);

            allJobs[m.id] = m.body;
            
            callFinishListeners(finishListeners, m.id, m.body);

            wrap.busy = false;
            assignFromQueue();
        } else if (m.type == "jobWaitingOnDependency") {
            const depId = m.dependencyId;
            const jobId = m.id;

            if (depId in allJobs) {
                sendDependencyComplete(worker, jobId, depId, allJobs[depId]);
            } else {
                wrap.busy = false;
                addFinishListener(finishListeners, depId, function (finishedContext) {
                    wrap.busy = true;
                    sendDependencyComplete(worker, jobId, depId, finishedContext);
                });
            }

            //noteJobDependency is true if it was successful (i.e. there were no circular dependencies)
            if (noteJobDependency(jobDependencyGraph, finishListeners, depId, jobId) == true) {
                assignFromQueue();
            }
        } else if(m.type == "jobError") {
            const id = m.id;
            if(id in callbacks) {
                callbacks[id]({
                    success: "COMPILATION_FAILED",
                    error: m.error,
                    fileAddress: id
                })
            }
        }
    });

    return wrap;
}

/**
 * 
 * @param {Worker} worker 
 * @param {string} jobId
 * @param {string} depId 
 * @param {import("./worker.js").MaybeCompilation} maybeCompilation 
 */
function sendDependencyComplete(worker, jobId, depId, maybeCompilation) {
    worker.postMessage({
        type: "dependencyComplete",
        id: jobId,
        dependencyId: depId,
        body: maybeCompilation
    });
}

/**
 * 
 * @param {t_allJobs} allJobs 
 * @param {t_finishListeners} finishListeners 
 */
function clearNonExistantFinishListeners(allJobs, finishListeners) {
    for (const jobId in finishListeners) {
        if (!(jobId in allJobs)) {
            callFinishListeners(finishListeners, jobId, {
                success: "DOES_NOT_EXIST"
            });
        }
    }
}

/**
 * 
 * @param {t_finishListeners} finishListeners 
 * @param {string[]} circle 
 */
function finishCircle(finishListeners, circle) {
    for (const jobId of circle) {
        callFinishListeners(finishListeners, jobId, {
            success: "COMPILATION_FAILED",
            fileAddress: jobId
        });
    }
}

/**
 * 
 * @param {t_jobDependencyGraph} jobDependencyGraph 
 * @param {t_finishListeners} finishListeners 
 * @param {string} depId
 * @param {string} jobId 
 * @returns {boolean} false if there is a circular dependency; true otherwise
 */
function noteJobDependency(jobDependencyGraph, finishListeners, depId, jobId) {
    if (!(jobId in jobDependencyGraph)) jobDependencyGraph[jobId] = {};
    if (!(depId in jobDependencyGraph)) jobDependencyGraph[depId] = {};

    jobDependencyGraph[jobId][depId] = jobDependencyGraph[depId];

    const circle = checkForCircularDependencies(jobDependencyGraph, depId, jobId);
    if (circle !== undefined) {
        finishCircle(finishListeners, circle);
        return false;
    } else {
        return true;
    }
}

/**
 * 
 * @param {t_jobDependencyGraph} jobDependencyGraph
 * @param {string} checkFrom 
 * @param {string} original 
 * @param {string[] | undefined} path 
 * @returns {string[] | undefined}
 */
function checkForCircularDependencies(jobDependencyGraph, checkFrom, original, path) {
    if (path === undefined) path = [original];

    for (const dependsOn in jobDependencyGraph[checkFrom]) {
        if (dependsOn == original) {
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

/**
 * 
 * @param {t_finishListeners} finishListeners
 * @param {string} jobId 
 * @param {import("./worker").MaybeCompilation} finishedContext
 */
function callFinishListeners(finishListeners, jobId, finishedContext) {
    if (jobId in finishListeners) {
        const listeners = finishListeners[jobId];
        while (listeners.length > 0) {
            listeners.shift()(finishedContext)
        }
    }
}

/**
 * 
 * @param {t_finishListeners} finishListeners 
 * @param {string} jobId 
 * @param {finishListener} callback 
 */
function addFinishListener(finishListeners, jobId, callback) {
    if (!(jobId in finishListeners)) finishListeners[jobId] = [];
    finishListeners[jobId].push(callback);
}