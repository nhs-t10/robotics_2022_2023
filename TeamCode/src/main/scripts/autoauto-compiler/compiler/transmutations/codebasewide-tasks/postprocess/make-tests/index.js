"use strict";

var path = require("path");
const commandLineInterface = require("../../../../../../command-line-interface");

var maketest = require("./make-test");

/**
 * @type {import("../../..").CodebaseTransmutateFunction}
 */
module.exports = function(context, contexts) {
    
    if(commandLineInterface["make-tests"] == false) return;
    
    
    var testDir = path.join(context.testRoot, "org/firstinspires/ftc/teamcode/unitTests/__testedautoautos");
    

    var testRecords = contexts
    .filter(x=>x.success == "SUCCESS")
    .map(x=>x.fileContext)
    .filter(x=> "write-to-output-file" in x.inputs)
    .map(x=>({
        frontmatter: x.fileFrontmatter,
        className: x.resultBaseFileName.split(".")[0],
        package: x.inputs["get-result-package"]
    }));
    
    var fileWrittenIn = maketest(testRecords, testDir);
    
    context.writtenFiles[fileWrittenIn] = true;
}

