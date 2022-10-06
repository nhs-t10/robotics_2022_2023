"use strict";

const commandLineInterface = require("../../../../../../command-line-interface/index.js");
const processHistory = require("./process-history.js");
const path = require("path");

/**
 * @type {import("../../..").CodebaseTransmutateFunction}
 */
module.exports = async function(context, contexts) {
    
    const srcDirectories = context.sourceRoots;
    const assetsDir = context.assetsRoot;
    const genDirectory = context.resultRoot;
    
    if (commandLineInterface["build-history"]) {
        var buildHistoryFile = await processHistory(srcDirectories, assetsDir, genDirectory)
        context.writtenFiles[buildHistoryFile] = true;
    }
}