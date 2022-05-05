var fs = require("fs");
var crypto = require("crypto");
var path = require("path");
const safeFsUtils = require("../../../../../../../script-helpers/safe-fs-utils");

var directory = __dirname.split(path.sep);

var srcDirectory = directory.slice(0, directory.indexOf("src") + 1).join(path.sep);
var assetsDir = path.join(srcDirectory, "main/assets");

module.exports = function(number, pngBuffer) {
    safeFsUtils.safeWriteFileEventually(path.join(assetsDir, "buildimgs/" + number + ".png"), pngBuffer);
    return "buildimgs/" + number + ".png";
}