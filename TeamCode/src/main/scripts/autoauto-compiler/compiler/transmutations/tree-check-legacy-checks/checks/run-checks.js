var fs = require("fs");
var path = require("path");
var androidStudioLogging = require("../../../../../script-helpers/android-studio-logging");

module.exports = function (ast, filename, frontmatter, checks) {
    var failed = false;
    for(var i = 0; i < checks.length; i++) {
        var check = checks[i];

        var tag = check.summary;
        
        try {
            var res = check.run(ast, frontmatter);
            if(res) {
                androidStudioLogging.sendTreeLocationMessage(res, filename);
                
                if(shouldStopAnalysis(res)) {
                    failed = true;
                    break;
                }
            }
        } catch(e) {
            if(e instanceof Error) tag += " | failed to execute";
            androidStudioLogging.sendTreeLocationMessage({
                kind: "ERROR",
                text: tag, 
                original: e.toString() + "\n" + (e.stack||"")
            }, filename);
            failed = true;
            break;
        }
    }
    return !failed;
}

function shouldStopAnalysis(messageArray) {
    if(typeof messageArray.find != "function") messageArray = [messageArray];
    
    return !!messageArray.find(x=>x instanceof Error || x.kind == "ERROR" || x.fail == true);
}