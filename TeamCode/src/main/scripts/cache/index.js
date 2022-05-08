var crypto = require("crypto");

var fs = require("fs");
var path = require("path");

var safeFsUtils = require("../script-helpers/safe-fs-utils");
const structuredSerialise = require("../script-helpers/structured-serialise");
const cleanOldCache = require("./clean-old-cache");
const cacheMetadata = require("./cache-metadata");
const androidStudioLogging = require("../script-helpers/android-studio-logging");

const CACHE_DIR = findCacheDirectory();
const CACHE_MAX_BYTES = 20_000_000; //20 MB
const CACHE_META_FILE = path.join(__dirname, ".cache.meta.json");


var cacheMeta = cacheMetadata(CACHE_META_FILE);

cleanOldCache(cacheMeta.getDataObject(), CACHE_DIR, CACHE_MAX_BYTES);

module.exports = {
    save: function(key, value) {
        var encodedKey = sha(key);
        var filename = keyFile(encodedKey);
        var dataBuffer = serialiseData(value);

        cacheMeta.updateKey(encodedKey, filename, dataBuffer.length);

        safeFsUtils.safeWriteFile(filename, dataBuffer);
    },
    get: function(key, defaultValue) {
        var encodedKey = sha(key);
        
        var file = keyFile(encodedKey);
        
        if(fs.existsSync(file)) return deserialiseData(fs.readFileSync(file), defaultValue);
        else return defaultValue;
    },
    remove: function(key) {
        var encodedKey = sha(key);
        var file = keyFile(encodedKey);
        cacheMeta.removeKey(encodedKey);
        if(fs.existsSync(file)) fs.unlinkSync(file);
    }
}

function findCacheDirectory() {
    var SIGIL = ".autoauto-compiler-cache-directory";

    var gitRoot = safeFsUtils.getGitRootDirectory();
    var folder = "";
    if(gitRoot) {
        var gradleFolder = path.join(gitRoot, ".gradle");
        if(fs.existsSync(gradleFolder)) {
            folder = path.join(gradleFolder, SIGIL)
        } else {
            folder = path.join(gitRoot, SIGIL);
            safeFsUtils.addToGitignore(SIGIL + "/**");
        }
    } else {
        folder = path.join(require("os").homedir(), SIGIL);
    }

    if(!fs.existsSync(folder)) safeFsUtils.createDirectoryIfNotExist(folder);
    return folder;
}

function serialiseData(data) {
    try {
        return structuredSerialise.toBuffer(data);
    } catch(e) {
        console.error(data);
        throw new Error("Couldn't serialise. " + e.message);
    }
}

function deserialiseData(dataBuffer, defaultValue) {
    try {
        if(isStructuredSerialised(dataBuffer)) return structuredSerialise.fromBuffer(dataBuffer, defaultValue);
        else return JSON.parse(dataBuffer.toString());
    } catch(e) {
        androidStudioLogging.sendTreeLocationMessage(e, "", "ERROR");
        return defaultValue;
    }
}

function isStructuredSerialised(dataBuffer) {
    var maybeMagic = dataBuffer.slice(0, structuredSerialise.magic.length);
    if(maybeMagic.join(",") == structuredSerialise.magic.join(",")) return true;
    else return false;
}

function keyFile(encodedKey, n, pfx) {
    if(n === undefined) n = 2;
    if(!pfx) pfx = CACHE_DIR;

    return path.join(pfx, encodedKey.substring(0,n), encodedKey.substring(n) + ".cached");
}

function sha(k) {
    return crypto.createHash("sha256").update(JSON.stringify(k)).digest("hex");
}

