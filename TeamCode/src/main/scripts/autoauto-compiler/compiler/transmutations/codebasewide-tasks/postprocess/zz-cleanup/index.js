const safeFsUtils = require("../../../../../../script-helpers/safe-fs-utils");
const deleteOldGeneratedFiles = require("./delete-old-generated-files");

module.exports = function (codebaseContext, contexts) {
    var newFiles = Object.keys(codebaseContext.writtenFiles);
    
    for(const ctx of contexts) {
        newFiles = newFiles.concat(Object.keys(ctx.writtenFiles));
    }

    safeFsUtils.cleanDirectory(contexts[0].resultRoot, newFiles);
    
    deleteOldGeneratedFiles();
}