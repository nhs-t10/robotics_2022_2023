"use strict";

const { send } = require("process");
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
 * @param {import("./worker.js").MaybeCompilation} finishedContext
 */

/**
 * @typedef {Object.<string, finishListener[]>} t_finishListeners
 */

/**
 * @typedef {Object.<string, import("./worker.js").MaybeCompilation>} t_allJobs 
 * A map of jobs, keyed by source file.
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
 * @typedef {import("./transmutations").TransmutateContext} TransmutateContext
 */

/**
 * @typedef {object} workerWrap
 * @property {boolean} busy
 * @property {(fileContext:TransmutateContext, job:jobCallback)=>void} assignJob
 * @property {()=>void} close
 * @property {boolean} mayExpectMoreJobs Whether the worker may expect more jobs going into the pool. Used in resolving dependencies
 * 
 */

/**
 * @typedef {object} workerPool
 * @property {(fileContext: TransmutateContext, cb: jobCallback) => undefined} giveJob;
 * @property {(fileContext: TransmutateContext, log: import("../../script-helpers/android-studio-logging").AndroidStudioMessage[] | undefined) => undefined} addFinishedJobFromCache;
 * @property {() => Promise<void>} finishGivingJobs
 * @property {() => undefined} close
 */

/**
 * 
 * @returns {workerPool}
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

            addInProgressJob(allJobs, fileContext);

            if (w) {
                w.assignJob(fileContext, cb);
            } else {
                queue.push({ fileContext: fileContext, cb: cb });
            }
        },
        addFinishedJobFromCache: function (fileContext, log) {
            const id = fileContext.sourceFullFileName;
            finishJob(finishListeners, allJobs, id, {
                success: "SUCCESS",
                fileContext: fileContext,
                log: log || []
            });
        },
        finishGivingJobs: async function () {
            await stopExpectingMoreJobs(pool);
            clearNonExistantFinishListeners(allJobs, finishListeners);
        },
        close: function () {
            pool.forEach(x => x.close());
        }
    }

}

function stopExpectingMoreJobs(pool) {
    return new Promise(function (resolve, reject) {
        for (const worker of pool) {
            worker.mayExpectMoreJobs = false;
        }
        resolve();
    });
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
        workerData: process.argv.slice(2),
        argv: process.argv.slice(2)
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
    var wrap = {
        mayExpectMoreJobs: true,
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

        addFinishListener(finishListeners, id, cb);
        addFinishListener(finishListeners, id, assignFromQueue);

        worker.postMessage({
            type: "newJob",
            body: job,
            id: id
        });
    }

    function assignFromQueue() {
        if (queue.length > 0) {
            const n = queue.shift();
            assignJob(n.fileContext, n.cb);
        } else {
            wrap.busy = false;
        }
    }

    worker.on("message", function (/** @type {import("./worker.js").InterThreadMessage} */m) {
        if (m.type == "jobDone") {
            finishJob(finishListeners, allJobs, m.id, m.body);
        } else if (m.type == "jobWaitingOnDependency") {
            const depId = m.dependencyId;
            const jobId = m.id;

            if (alreadyFinishedJob(depId, allJobs)) {
                sendDependencyComplete(worker, jobId, depId, allJobs[depId], allJobs);
            } else if (jobExists(depId, allJobs) || wrap.mayExpectMoreJobs) {
                assignFromQueue();
                addFinishListener(finishListeners, depId, function (finishedContext) {
                    wrap.busy = true;
                    sendDependencyComplete(worker, jobId, depId, finishedContext, allJobs);
                });
            } else {
                sendNonexistDependency(worker, jobId, depId);
            }

            //noDependencyCycles is true if it was successful (i.e. there were no circular dependencies)
            //if it failed, it will cancel all the jobs in the circle, which are waiting on dependencies.
            noDependencyCycles(jobDependencyGraph, finishListeners, depId, jobId);
        } else if (m.type == "jobError") {
            const id = m.id;

            finishJob(finishListeners, allJobs, m.id, {
                success: "COMPILATION_FAILED",
                error: m.error,
                fileAddress: id
            });
        } else {
            console.error("UNKNOWN MESSAGE TYPE ", m.type);
        }
    });

    return wrap;
}

/**
 * 
 * @param {string} depId 
 * @param {t_allJobs} allJobs
 * @returns {boolean} 
 */
function alreadyFinishedJob(depId, allJobs) {
    return (depId in allJobs) && allJobs[depId].success !== "IN_PROGRESS";
}

/**
 * 
 * @param {string} depId 
 * @param {t_allJobs} allJobs 
 * @returns {boolean}
 */
function jobExists(depId, allJobs) {
    return depId in allJobs
}

/**
 * 
 * @param {t_allJobs} allJobs
 * @param {TransmutateContext} job 
 */
function addInProgressJob(allJobs, job) {
    allJobs[job.sourceFullFileName] = {
        success: "IN_PROGRESS",
        fileContext: job
    };
}

/**
 * 
 * @param {Worker} worker 
 * @param {string} jobId
 * @param {string} depId 
 * @param {import("./worker.js").MaybeCompilation} maybeCompilation 
 * @param {t_allJobs} allJobs
 */
function sendDependencyComplete(worker, jobId, depId, maybeCompilation, allJobs) {
    
    allJobs[jobId].fileContext.dependsOn[depId] = allJobs[depId].fileContext.cacheKey;
    
    worker.postMessage({
        type: "dependencyComplete",
        id: jobId,
        dependencyId: depId,
        body: maybeCompilation
    });
}

/**
 * 
 * @param {*[]} arr 
 * @param {*} itm 
 */
function pushUniquely(arr, itm) {
    if(arr.includes(itm) == false) arr.push(itm);
}

/**
 * 
 * @param {t_allJobs} allJobs 
 * @param {t_finishListeners} finishListeners 
 */
function clearNonExistantFinishListeners(allJobs, finishListeners) {
    for (const jobId in finishListeners) {
        if (!(jobId in allJobs)) {
            finishJob(finishListeners, allJobs, jobId, {
                success: "DOES_NOT_EXIST"
            });
        }
    }
}

/**
 * 
 * @param {Worker} worker 
 * @param {string} jobId
 * @param {string} depId 
 */
function sendNonexistDependency(worker, jobId, depId) {
    worker.postMessage({
        type: "dependencyComplete",
        id: jobId,
        dependencyId: depId,
        body: {
            success: "DOES_NOT_EXIST"
        }
    });
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
function noDependencyCycles(jobDependencyGraph, finishListeners, depId, jobId) {
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
 * @param {t_allJobs} allJobs
 * @param {string} jobId 
 * @param {import("./worker").MaybeCompilation} finishedContext
 */
function finishJob(finishListeners, allJobs, jobId, finishedContext) {
    allJobs[jobId] = finishedContext;

    callFinishListeners(finishListeners, jobId, finishedContext);
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