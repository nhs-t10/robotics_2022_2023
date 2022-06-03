const fs = require("fs");
const path = require("path");
const androidStudioLogging = require("../../script-helpers/android-studio-logging");
const safeFsUtils = require("../../script-helpers/safe-fs-utils")

/**
 * 
 */
module.exports = function() {
    var gradleRoot = safeFsUtils.getGradleRootDirectory();
    
    if(gradleRoot === undefined) {
        androidStudioLogging.warning("The Autoauto compiler couldn't detect a Gradle project. Scanning for autoauto files in the current directory; building in the current directory.");
        const cwd = process.cwd();
        return {
            src: cwd,
            gen: cwd,
            asset: cwd,
            test: cwd
        };
    }
    
    var srcDir = path.join(gradleRoot, "src", "main", "java");
    if(!fs.existsSync(srcDir)) {
        androidStudioLogging.error(`The folder src/main/java does not exist in the gradle project root (${gradleRoot})!`);
        process.exit(1);
    }
    
    const assetDir = path.join(gradleRoot, "src", "main", "assets");
    
    const genDir = path.join(gradleRoot, "gen");
    
    const testDir = path.join(gradleRoot, "src", "test", "java");
    
    return {
        src: srcDir,
        gen: genDir,
        asset: assetDir,
        test: testDir
    };
}