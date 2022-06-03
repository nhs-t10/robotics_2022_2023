var path = require("path");
const { safeWriteFile } = require("../../../../../../script-helpers/safe-fs-utils");

var maketest = require("./make-test");

/**
 * 
 * @param {*} context 
 * @param {(import("../../../index").TransmutateContext)[]} contexts
 */
module.exports = function(context, contexts) {
    var testDir = path.join(context.testRoot, "org/firstinspires/ftc/teamcode/unitTests/__testautoauto");
    
    var testRecords = contexts
    .filter(x=>x.status == "pass" && ("get-result-package" in x.inputs))
    .map(x=>({
        frontmatter: x.fileFrontmatter,
        className: x.resultBaseFileName.split(".")[0],
        package: x.inputs["get-result-package"]
    }));
    
    var fileWrittenIn = maketest(testRecords, testDir);
    
    addGitignoreOfTesters(testDir);
    
    context.writtenFiles[fileWrittenIn] = true;
}

function addGitignoreOfTesters(testDir) {
    safeWriteFile(path.join(testDir, ".gitignore"), "*.java");
}
