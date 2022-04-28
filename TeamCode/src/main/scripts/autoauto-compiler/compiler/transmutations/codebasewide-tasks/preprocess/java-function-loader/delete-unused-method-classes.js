var fs = require("fs");
var path = require("path");

var directory = __dirname.split(path.sep);
var rootDirectory = directory.slice(0, directory.indexOf("TeamCode")).join(path.sep);
var robotFunctionsDirectory = path.join(rootDirectory, "TeamCode/gen/org/firstinspires/ftc/teamcode/auxilary/dsls/autoauto/runtime/robotfunctions");

/**
 * 
 * @param {string[]} methodClassNames An array of class names that are used in this build.
 */
module.exports = function(methodClassNames) {
    var javaFileNames = fs.readdirSync(robotFunctionsDirectory);
    for(var i = 0; i < javaFileNames.length; i++) {
        if(!javaFileNames[i].endsWith(".java")) continue;
        var javaClassName = /(\w+)(\.java)?$/.exec(javaFileNames[i])[1];
        if(!methodClassNames.includes(javaClassName)) {
            fs.unlinkSync(path.join(robotFunctionsDirectory, javaClassName + ".java"));
        }
    }
}