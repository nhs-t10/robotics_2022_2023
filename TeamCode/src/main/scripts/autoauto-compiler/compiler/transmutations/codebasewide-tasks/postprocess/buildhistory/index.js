const commandLineInterface = require("../../../../../../command-line-interface/index.js");
var processHistory = require("./process-history.js");

module.exports = async function(context, contexts) {
    
    if (commandLineInterface["build-history"]) {
        var buildHistoryFile = await processHistory();
        context.writtenFiles[buildHistoryFile] = true;
    }
}