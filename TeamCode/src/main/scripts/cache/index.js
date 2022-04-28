var crypto = require("crypto");

var fs = require("fs");
var path = require("path");
const commandLineInterface = require("../command-line-interface");

var safeFsUtils = require("../script-helpers/safe-fs-utils");
const structuredSerialise = require("../script-helpers/structured-serialise");

const CACHE_DIR = findCacheDirectory();
const CACHE_MAX_BYTES = 20_000_000; //20 MB
const CACHE_META_FILE = path.join(__dirname, ".cache.meta.json");

if(!fs.existsSync(CACHE_META_FILE)) fs.writeFileSync(CACHE_META_FILE, "{}");
var cacheMeta = require(CACHE_META_FILE);

cleanOldCache(cacheMeta);

module.exports = {
    save: function(key, value) {
        var encodedKey = sha(key);
        var filename = keyFile(encodedKey);
        var dataBuffer = serialiseData(value);

        cacheMeta[encodedKey] = { key: encodedKey, file: filename, size: dataBuffer.length, lastWrite: Date.now() };

        safeFsUtils.createDirectoryIfNotExist(filename);
        fs.writeFileSync(filename, dataBuffer);
    },
    get: function(key, defaultValue) {
        var encodedKey = sha(key);
        
        var file = keyFile(encodedKey);
        
        if(fs.existsSync(file)) return deserialiseData(fs.readFileSync(file));
        else return defaultValue;
    },
    remove: function(key) {
        var encodedKey = sha(key);
        var file = keyFile(encodedKey);
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

    if(!fs.existsSync(folder)) fs.mkdirSync(folder);
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

function deserialiseData(dataBuffer) {
    if(isStructuredSerialised(dataBuffer)) return structuredSerialise.fromBuffer(dataBuffer);
    else return JSON.parse(dataBuffer.toString());
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

function cleanOldCache(cacheMeta) {
    const metaEntries = Object.values(cacheMeta);
    let cacheFiles = [], totalSize = 0, oldestCacheEntry = undefined, oldestLastWrite = 0;
    
    for(const cacheMetaEntry of metaEntries) {

        cacheFiles.push(cacheMetaEntry.file);

        if(cacheMetaEntry.size > CACHE_MAX_BYTES) removeMetaEntry(cacheMeta, cacheMetaEntry);
        else totalSize += cacheMetaEntry.size;

        if(cacheMetaEntry.lastWrite < oldestLastWrite) {
            oldestTime = cacheMetaEntry.lastWrite;
            oldestCacheEntry = cacheMetaEntry;
        }
    }

    safeFsUtils.cleanDirectory(CACHE_DIR, cacheFiles, true);

    if(totalSize > CACHE_MAX_BYTES) removeMetaEntry(cacheMeta, oldestCacheEntry);
}

function removeMetaEntry(cacheMeta, cacheMetaEntry) {
    console.warn("Flushing cache entry " + cacheMetaEntry.key);
    delete cacheMeta[cacheMetaEntry.key];
    fs.unlinkSync(cacheMetaEntry.file);
}

process.on("exit", function() {
    fs.writeFileSync(CACHE_META_FILE, JSON.stringify(cacheMeta));
});