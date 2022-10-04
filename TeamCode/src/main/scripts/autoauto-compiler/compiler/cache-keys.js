const { cachedSafeReadFile } = require("../../script-helpers/safe-fs-utils");
const { sha } = require("../../script-helpers/sha-string");

const allCacheKeys = {};
const cacheKeyListeners = {};

module.exports = {
    makeCacheKey: makeCacheKey,
    finishCacheKeys: finishCacheKeys
};

/**
 * 
 * @param {import("./transmutations").TransmutateContext} fileContext 
 */
async function makeCacheKey(fileContext, environmentHash) {
    const id = fileContext.sourceFullFileName;
    
    const readFileShas = fileContext.readsAllFiles.map(x => sha(cachedSafeReadFile(x))).join("\0");
    const transmutationIdList = fileContext.transmutations.map(x => x.id).join("\t");

    const keyDataToSha = [environmentHash, readFileShas,
        fileContext.sourceFullFileName, fileContext.fileContentText, transmutationIdList];
        
    for(const dependencyId in fileContext.dependsOn) {
        const depFreshKey = await getDependencyKey(dependencyId);
        keyDataToSha.push(dependencyId + ":" + depFreshKey);
    }

    const key = sha(keyDataToSha.join("\0"));
    
    resolveKey(id, key);
    
    return key;
}

function resolveKey(id, key) {
    allCacheKeys[id] = key;
    
    if (cacheKeyListeners[id]) {
        for (const l of cacheKeyListeners[id]) l();
        
        delete cacheKeyListeners[id];
    }
}

/**
 * 
 * @param {string} id 
 * @returns Promise<string>
 */
function getDependencyKey(id) {
    if (cacheKeyListeners[id] === undefined) cacheKeyListeners[id] = [];
    
    return new Promise(function(resolve, reject) {
        if(allCacheKeys[id]) {
            resolve(allCacheKeys[id]);
        } else {
            cacheKeyListeners[id].push(function() {
                resolve(allCacheKeys[id]);
            });
        }
    });
    
}

function finishCacheKeys() {
    for(const danglingKey in cacheKeyListeners) {
        resolveKey(danglingKey, "");
    }
}