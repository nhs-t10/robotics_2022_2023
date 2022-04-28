var workerThreads = require("worker_threads");
var commandLineInterface = require("../../command-line-interface");

module.exports = function () {
    var pool = [], queue = [];

    var workerCount = commandLineInterface.threads;

    //avoid the overhead of making a pool when only 1 thread is needed
    if(workerCount <= 1) return fakeWorkerPool();

    for(var i = workerCount; i>=0; i--) pool.push(createWorkerWrap(queue));

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

function createWorkerWrap(queue) {
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
    function assignJob(job, cb) {
        wrap.busy = true;
        var id = jobNumber++;
        callbacks[id] = cb;
        worker.postMessage({
            type: "newJob",
            body: job,
            id: id
        });
    }

    worker.on("message", function(m) {
        if(m.type == "jobDone") {
            if(callbacks[m.id]) callbacks[m.id](m.body);

            if(queue.length > 0) {
                var n = queue.shift();
                assignJob(n.job, n.cb);
            }
            else {
                wrap.busy = false;
            }
        }
    });

    return wrap;
}