const commandLineInterface = require("../../../../../../command-line-interface/index.js");
const processHistory = require("./process-history.js");
const path = require("path");

/**
 * 
 * @param {import("../../../../transmutations").TransmutateContext} context
 * @param {*} contexts 
 */
module.exports = async function(context, contexts) {
    
    const srcDirectory = context.sourceRoot;
    const assetsDir = context.assetsRoot;
    const genDirectory = context.resultRoot;
    
    if (commandLineInterface["build-history"]) {
        var buildHistoryFile = await processHistory(srcDirectory, assetsDir, genDirectory);
        context.writtenFiles[buildHistoryFile] = true;
    }
}