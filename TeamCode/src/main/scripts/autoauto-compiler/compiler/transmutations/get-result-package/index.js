var path = require("path");

/**
 * 
 * @param {import("../index").TransmutateContext} context 
 */
module.exports = function (context) {
    var pkgPath = context.resultDir.replace(context.resultRoot, "");
    
    if (pkgPath.startsWith(path.sep)) pkgPath = pkgPath.substring(1);
    
    const pkg = pkgPath.replace(new RegExp("\\" + path.sep, "g"), ".");

    context.output = pkg;
    context.status = "pass";
}