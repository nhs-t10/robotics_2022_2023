var cache = require("../../../../../../../cache");

var pngFromHash = require("./create-png-from-hash.js");
var deltaHashDirectory = require("./delta-hash-directory.js");

module.exports = async function(buildNumber, directory, ignored) {
    var oldHash = getPreviousBuildForDelting();
    
    var hexHash = await deltaHashDirectory(directory, oldHash.c, ignored);
    
    var nonzeroBuildAddress = pngFromHash(buildNumber, hexHash.diff) || {address: oldHash.p, colors: ""};

    console.log(oldHash);
    
    cache.save("last-build-pixels", {
        c: hexHash.hash,
        p: nonzeroBuildAddress.address
    });
    
    console.log(cache.get("last-build-pixels", 128));
    
    return {
        imageAddress: nonzeroBuildAddress.address,
        colors: nonzeroBuildAddress.colors,
        perceptualDiff: hexHash.diff
    };
}

function getPreviousBuildForDelting() {
    const defaultRes = { c: "", p: "buildimgs/0.png" };
    const cacheEntry = cache.get("last-build-pixels", defaultRes);

    if ("c" in cacheEntry) return cacheEntry;
    else return defaultRes;
}