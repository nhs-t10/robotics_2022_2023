var path = require("path");
const safeFsUtils = require("../../../../../../../script-helpers/safe-fs-utils");


module.exports = function (number, pngBuffer, assetsDir) {
    safeFsUtils.safeWriteFileEventually(path.join(assetsDir, "buildimgs/" + number + ".png"), pngBuffer);
    return "buildimgs/" + number + ".png";
}