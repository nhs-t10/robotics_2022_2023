var path = require("path");
var safeFsUtils = require("../../../../../../script-helpers/safe-fs-utils");

var template = safeFsUtils.safeReadFile(__dirname + "/test.notjava").toString();


/**
 * @typedef {object} TestRecord
 * @property {string} className
 * @property {string} package
 * @property {object} frontMatter
 */

/**
 * 
 * @param {TestRecord[]} testRecords 
 * @param {string} testsDir 
 * @param {string} testPackage 
 * @returns 
 */
module.exports = function(testRecords, testsDir) {

    var testName = "TestAutoautos";

    var testMethods = testRecords.map((x,i)=>`
    @Test
    public void runTest${i}_${x.className}() {
        FeatureManager.logger.setRecordLogHistory(true);
        assertTrue(OpmodeTester.runTestOn(new ${x.package}.${x.className}()));
        ${makeAssert(x.frontMatter)}
        FeatureManager.logger.setRecordLogHistory(false);
    }
    `).join("");

    var result = template
        .replace("TESTMETHODS", testMethods);

    var resultFile = path.join(testsDir, testName + ".java");

    safeFsUtils.safeWriteFileEventually(resultFile, result);
}

function makeAssert(frontMatter) {
    if(frontMatter.expectedTestOutput === undefined) return "";
    else return `assertThat("Log printed correctly", FeatureManager.logger.getLogHistory(), containsString(${JSON.stringify(frontMatter.expectedTestOutput)}));`; 
}