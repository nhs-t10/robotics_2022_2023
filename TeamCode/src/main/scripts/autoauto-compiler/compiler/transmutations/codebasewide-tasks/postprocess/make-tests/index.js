var path = require("path");

var maketest = require("./make-test");

/**
 * 
 * @param {*} context 
 * @param {(import("../../../index").TransmutateContext)[]} contexts
 */
module.exports = function(context, contexts) {
    var testDir = path.join(contexts[0].resultRoot, "org/firstinspires/ftc/teamcode/unitTests");
    
    var testRecords = contexts
    .filter(x=>x.status == "pass")
    .map(x=>({
        frontMatter: x.fileFrontmatter,
        className: x.resultBaseFileName.split(".")[0],
        package: x.inputs["get-result-package"]
    }));
    
    var fileWrittenIn = maketest(testRecords, testDir);
    
    context.writtenFiles[fileWrittenIn] = true;
}
