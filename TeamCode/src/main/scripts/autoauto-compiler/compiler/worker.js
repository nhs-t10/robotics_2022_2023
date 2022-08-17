"use strict";

const { inspect } = require("util");
const workerThreads = require("worker_threads");
const androidStudioLogging = require("../../script-helpers/android-studio-logging");
const { structuredClone } = require("../../script-helpers/structured-serialise");
const compileFile = require("./compile-file");

var listenersForMainThread = { message: () => undefined };

/**
 * @type {Object.<string, dependencyListener[]>}
 */
var dependencyListeners = {};

if (workerThreads.isMainThread) {
    module.exports = {
        //exposed for the benefit of the "parent" side
        postMessage: handleParentMessage,
        unref: () => undefined,
        on: (eType, eListener) => listenersForMainThread.message = eListener,

        //exposed for the "child" side
        requestDependencyToParent: requestDependencyToParent

    }
} else {
    workerThreads.parentPort.on("message", handleParentMessage);

    module.exports = {
        requestDependencyToParent: requestDependencyToParent
    }
}

/**
 * @typedef {object} NewJobMessage
 * @property {"newJob"} type
 * @property {string} id
 * @property {import("../compiler/transmutations/index").TransmutateContext} body 
 */

/**
 * @typedef {object} JobDoneMessage
 * @property {"jobDone"} type
 * @property {string} id
 * @property {MaybeCompilation} body
 */

/**
 * @typedef {object} dependencyCompleteMessage
 * @property {"dependencyComplete"} type
 * @property {string} dependencyId
 * @property {string} id
 * @property {MaybeCompilation} body
 */

/**
 * @typedef {object} jobWaitingOnDependencyMessage
 * @property {"jobWaitingOnDependency"} type
 * @property {string} dependencyId
 * @property {string} id
 */

/**
 * @typedef {object} jobErrorMessage
 * @property {"jobError"} type
 * @property {*} error
 * @property {string} id
 */

/**
 * @typedef {NewJobMessage|dependencyCompleteMessage|JobDoneMessage|jobWaitingOnDependencyMessage|jobErrorMessage} InterThreadMessage
 */


/**
 * 
 * @param {InterThreadMessage} m 
 */
async function handleParentMessage(m) {
    if (m.type == "newJob") {
        var evaledJob = await evaluateJob(m.body);
        sendMessageToParent({
            type: "jobDone",
            id: m.id,
            body: evaledJob
        });
    } else if (m.type == "dependencyComplete") {
        callDependencyListeners(m.dependencyId, m.body);
    }
}

/**
 * 
 * @param {string} depId 
 * @param {MaybeCompilation} dep
 */
function callDependencyListeners(depId, dep) {
    if (depId in dependencyListeners) {
        const listeners = dependencyListeners[depId];
        while (listeners.length > 0) {
            listeners.shift()(dep);
        }
    }
}

/**
 * @param {string} depId 
 * @param {dependencyListener} l
 */
function addDependencyListener(depId, l) {
    if (!(depId in dependencyListeners)) dependencyListeners[depId] = [];

    dependencyListeners[depId].push(l);
}

/**
 * @typedef {MaybeCompilationSucceeded|MaybeCompilationFailed|MaybeCompilationInProgress} MaybeCompilation
 */

/**
 * @typedef {Object} MaybeCompilationSucceeded
 * @property {"SUCCESS"} success
 * @property {import("../compiler/transmutations/index").TransmutateContext} fileContext
 * @property {AndroidStudioMessage} log
 */

/**
 * @typedef {Object} MaybeCompilationFailed
 * @property {"COMPILATION_FAILED"|"DOES_NOT_EXIST"} success
 * @property {import("../compiler/transmutations/index").TransmutateContext?} fileContext
 * @property {AndroidStudioMessage[]?} log
 * @property {*?} error
 * @property {string?} fileAddress
 */

/**
 * @typedef {object} MaybeCompilationInProgress
 * @property {"IN_PROGRESS"} success
 * @property {import("../compiler/transmutations/index").TransmutateContext} fileContext
 */

/**
 * @callback dependencyListener
 * @param {MaybeCompilation} resolvedDep
 * @returns {void}
 */


/**
 * 
 * @param {string} sourceFile 
 * @param {string} dependencyFile
 * @returns {Promise<import("../compiler/transmutations/index").TransmutateContext>}
 */
function requestDependencyToParent(sourceFile, dependencyFile) {

    const loggingGlobalState = androidStudioLogging.getGlobalState();

    return new Promise(function (resolve, reject) {
        addDependencyListener(dependencyFile, function (resolvedDep) {
            
            androidStudioLogging.setGlobalState(loggingGlobalState);

            if (resolvedDep.success === "COMPILATION_FAILED") {
                reject("Dependency " + dependencyFile + " didn't successfully compile. As such, this file couldn't compile.");
            } else if (resolvedDep.success === "DOES_NOT_EXIST") {
                reject("Dependency " + dependencyFile + " doesn't exist");
            } else if (resolvedDep.success == "SUCCESS") {
                if ("fileContext" in resolvedDep) {
                    const clonedFileContext = structuredClone(resolvedDep.fileContext);
                    resolve(clonedFileContext);
                } else {
                    reject("bad structure of filecontext!");
                }
            } else {
                console.debug(resolvedDep.success);
                console.debug(Object.keys(resolvedDep));
                reject("Malformed dependency result!");
            }
        });
        sendMessageToParent({
            type: "jobWaitingOnDependency",
            id: sourceFile,
            dependencyId: dependencyFile
        });
    })
}

/**
 * 
 * @param {InterThreadMessage} message 
 */
function sendMessageToParent(message) {
    if (workerThreads.isMainThread) {
        listenersForMainThread.message(message);
    } else {
        workerThreads.parentPort.postMessage(message);
    }
}


/**
 * 
 * @param {import("../compiler/transmutations/index").TransmutateContext} fileContext
 * @returns {Promise<MaybeCompilation>}
 */
async function evaluateJob(fileContext) {
    return await compileFile(fileContext);
}