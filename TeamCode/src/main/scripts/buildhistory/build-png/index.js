var fs = require("fs");
var path = require("path");

var cache = require("../../cache");

var pngFromHash = require("./create-png-from-hash.js");
var deltaHashDirectory = require("./delta-hash-directory.js");

module.exports = function(buildNumber, directory, ignored) {
    var hexHash = deltaHashDirectory(directory, ignored);
    var nonzeroBuildAddress = pngFromHash(buildNumber, hexHash.diff) || {address: hexHash.oldHash.p, colors: ""};

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