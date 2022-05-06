var processHistory = require("./process-history.js");

module.exports = async function(context, contexts) {
    
    var buildHistoryFile = await processHistory();
    
    console.log(buildHistoryFile);
    
    context.writtenFiles[buildHistoryFile] = true;
}