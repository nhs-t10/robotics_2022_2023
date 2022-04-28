var path = require("path");

var maketest = require("./make-test");

module.exports = function(context, contexts) {
    var testDir = path.join(contexts[0].sourceRoot, "test/java/org/firstinspires/ftc/teamcode/unitTests/__testedautoauto");
    
    var testRecords = contexts
    .filter(x=>x.status == "pass")
    .map(x=>({
        frontMatter: x.fileFrontmatter,
        className: x.resultBaseFileName.split(".")[0],
        package: x.inputs["get-result-package"]
    }));
    
    maketest(testRecords, testDir);
}
