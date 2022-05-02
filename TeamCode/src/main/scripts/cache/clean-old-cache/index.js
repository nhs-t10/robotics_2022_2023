const deleteOldVersions = require("./delete-old-versions");
const pruneToSize = require("./prune-to-size");
const worker_threads = require("worker_threads");

module.exports = function (cacheMeta, cacheFolder, cacheMaxBytes) {
    if(worker_threads.isMainThread) {
        deleteOldVersions();
        pruneToSize(cacheMeta, cacheFolder, cacheMaxBytes);
    }
}