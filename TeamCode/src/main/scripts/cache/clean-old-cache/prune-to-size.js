const safeFsUtils = require("../../script-helpers/safe-fs-utils");

module.exports = function(cacheMeta, cacheDir, cacheMaxBytes) {
    const metaEntries = Object.values(cacheMeta);
    let cacheFiles = [], totalSize = 0, oldestCacheEntry = undefined, oldestLastWrite = 0;

    for (const cacheMetaEntry of metaEntries) {

        cacheFiles.push(cacheMetaEntry.file);

        if (cacheMetaEntry.size > cacheMaxBytes) removeMetaEntry(cacheMeta, cacheMetaEntry);
        else totalSize += cacheMetaEntry.size;

        if (cacheMetaEntry.lastWrite < oldestLastWrite) {
            oldestTime = cacheMetaEntry.lastWrite;
            oldestCacheEntry = cacheMetaEntry;
        }
    }

    safeFsUtils.cleanDirectory(cacheDir, cacheFiles, true);

    if (totalSize > cacheMaxBytes) removeMetaEntry(cacheMeta, oldestCacheEntry);
}

function removeMetaEntry(cacheMeta, cacheMetaEntry) {
    if(cacheMetaEntry != undefined) {
        console.warn("Flushing cache entry " + cacheMetaEntry.key);
        delete cacheMeta[cacheMetaEntry.key];
        fs.unlinkSync(cacheMetaEntry.file);
    }
}