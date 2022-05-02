const safeFsUtils = require("../../script-helpers/safe-fs-utils");

module.exports = function () {
    safeFsUtils.cleanDirectory(__dirname + "/../.cache");
}