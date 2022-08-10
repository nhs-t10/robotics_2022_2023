"use strict";

const safeFsUtils = require("../../../../../../script-helpers/safe-fs-utils");
const deleteOldGeneratedFiles = require("./delete-old-generated-files");

/**
 * @type {import("../../..").CodebaseTransmutateFunction}
 */
module.exports = function (context, contexts) {
    var newFiles = Object.keys(context.writtenFiles);
    
    for(const ctx of contexts) {
        if(ctx.success === "SUCCESS") {
            newFiles.push(...Object.keys(ctx.fileContext.writtenFiles));
        }
    }
    

    safeFsUtils.cleanDirectory(context.resultRoot, newFiles);
    
    deleteOldGeneratedFiles();
}