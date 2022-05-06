const path = require("path");
const safeFsUtils = require("../../../../../../script-helpers/safe-fs-utils");

module.exports = function() {
    var toClean = path.join(__dirname, "/../../../../../../../java/test");
    
    safeFsUtils.cleanDirectory(toClean);
}
