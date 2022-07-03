var fs = require("fs");

module.exports = function(cacheMetaFile) {
    var cacheMetaObj = getCacheMeta(cacheMetaFile);
    
    registerExitHook(cacheMetaFile, cacheMetaObj);
    
    return {
        getDataObject: function() { return cacheMetaObj; },
        updateKey: function (encodedKey, filename, size) {
            cacheMetaObj[encodedKey] = { key: encodedKey, file: filename, size: size, lastWrite: Date.now() };
        },
        removeKey: function (encodedKey) {
            delete cacheMeta[encodedKey];
        }
    }
}

function getCacheMeta(cacheMetaFile) {
    if (!fs.existsSync(cacheMetaFile)) return {};
    else return loadJSONFile(cacheMetaFile);
}

function loadJSONFile(file) {
    try {
        return JSON.parse(fs.readFileSync(file))
    } catch(e) {}
    return {};
}

function registerExitHook(cacheMetaFile, cacheMetaObj) {
    process.on("exit", function () {
        fs.writeFileSync(cacheMetaFile, JSON.stringify(cacheMetaObj));
    });
}