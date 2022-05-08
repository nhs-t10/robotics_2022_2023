var cache = require("../../../../../../../cache");

var pngFromHash = require("./create-png-from-hash.js");
var deltaHashDirectory = require("./delta-hash-directory.js");

module.exports = async function (buildNumber, srcDir, ignored, assetsDir) {
    var oldHash = getPreviousBuildForDelting();
    
    var hexHash = await deltaHashDirectory(srcDir, oldHash.c, ignored);
    
    var nonzeroBuildAddress = pngFromHash(buildNumber, hexHash.diff, 0, assetsDir) || {address: oldHash.p, colors: ""};
    
    cache.save("last-build-pixels", {
        c: hexHash.hash,
        p: nonzeroBuildAddress.address
    });
    
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