const workerThreads = require("worker_threads");
const compileFile = require("./compile-file");

var listenersForMainThread = {};
var dependencyListeners = {};

if(workerThreads.isMainThread) {
    module.exports = {
        //exposed for the benefit of the "parent" side
        postMessage: handleParentMessage,
        unref: ()=>undefined,
        on: (eType, eListener)=>listenersForMainThread[eType] = eListener,
        
        //exposed for the "child" side
        requestDependencyToParent: requestDependencyToParent
        
    }
} else {
    process.argv = process.argv.concat(workerThreads.workerData);
    workerThreads.parentPort.on("message", handleParentMessage);
    
    module.exports = {
        requestDependencyToParent: requestDependencyToParent
    }
}

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

function callDependencyListeners(depId, dep) {
    if(depId in dependencyListeners) {
        const listeners = dependencyListeners[depId];
        while(listeners.length > 0) {
            listeners.shift()(dep);
        }
    }
}

function addDependencyListener(depId, l) {
    if(!(depId in dependencyListeners)) dependencyListeners[depId] = [];
    
    dependencyListeners[depId].push(l);
}

function requestDependencyToParent(sourceFile, dependency) {
    return new Promise(function(resolve, reject) {
        addDependencyListener(dependency, function(resolvedDep) {
            if(resolvedDep === undefined) {
                reject("Couldn't compile dependency " + dependency);
            } else if(resolvedDep === null) {
                reject("Dependency " + dependency + " doesn't exist");
            } else if(resolvedDep.success == false) {
                reject("Dependency " + dependency + " didn't successfully compile");
            } else {
                resolve(resolvedDep.fileContext);
            }
        })
        sendMessageToParent({
            type: "jobWaitOnDependency",
            id: sourceFile,
            dependencyId: dependency
        });
    })
}

function sendMessageToParent(message) {
    if(workerThreads.isMainThread) {
        listenersForMainThread.message(message);
    } else {
        workerThreads.parentPort.postMessage(message);
    }
}



async function evaluateJob(fileContext) {
    return await compileFile(fileContext);
}