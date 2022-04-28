var path = require("path");

module.exports = function (context) {
    var sD = context.resultDir.split(path.sep);

    context.output = sD.slice(sD.indexOf("gen") + 1).join(".");
    context.status = "pass";
}