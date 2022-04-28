var fs = require("fs");
var path = require("path");
var androidStudioLogging = require("../../../../../script-helpers/android-studio-logging");


var checks = loadChecksFromFiles(require("./check-file-names"));

module.exports = function(ast, filename) {
    var failed = false;
    for(var i = 0; i < checks.length; i++) {
        var check = checks[i];

        var tag = "[" + check.id + "] " + check.summary;
        
        try {
            var res = check.run(ast);
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


function loadChecksFromFiles(files) {
    return files.map(x=> {
        var check = require(x);
        check.id = path.basename(x).replace(".js", "").toUpperCase();
        return check;
    });
}