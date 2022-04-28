var workerThreads = require("worker_threads");
const compileFile = require("./compile-file");

if(workerThreads.isMainThread) {
    module.exports = evaluateJob;
} else {
    process.argv = process.argv.concat(workerThreads.workerData);
    workerThreads.parentPort.on("message", async function(m) {
        if(m.type == "newJob") {
            var evaledJob = await evaluateJob(m.body);
            
            workerThreads.parentPort.postMessage({
                type: "jobDone",
                id: m.id,
                body: evaledJob
            });
        }
    });
}




async function evaluateJob(fileContext) {
    return await compileFile(fileContext);
}