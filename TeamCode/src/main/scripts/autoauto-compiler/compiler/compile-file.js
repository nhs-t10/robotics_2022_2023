"use strict";

const androidStudioLogging = require("../../script-helpers/android-studio-logging");


/**
 * 
 * @param {import("./transmutations").TransmutateContext} fileContext 
 * @returns {Promise<import("./worker").MaybeCompilation>}
 */
module.exports = async function (fileContext) {
    androidStudioLogging.beginOutputCapture();
    var sucStatus = await compileFile(fileContext);
    var log = androidStudioLogging.getCapturedOutput();

    return {
        success: sucStatus,
        fileContext: fileContext,
        log: log,
        fileAddress: fileContext.sourceFullFileName
    }
}

/**
 * Mutates the given fileContext into a completed, compiled version.
 * @param {import("./transmutations").TransmutateContext} fileContext 
 * @returns {Promise<"SUCCESS"|"COMPILATION_FAILED">} Success state
 */
async function compileFile(fileContext) {
    for (const mut of fileContext.transmutations) {
        const mutRan = await tryRunTransmutation(mut, fileContext);

        if (!mutRan) return "COMPILATION_FAILED";
    }

    return "SUCCESS";
}

/**
 * 
 * @param {*} transmutation 
 * @param {import("./transmutations").TransmutateContext} fileContext 
 * @returns 
 */
async function tryRunTransmutation(transmutation, fileContext) {
    delete fileContext.status;

    try {
        await runTransmutation(transmutation, fileContext);
    } catch(e) {
        androidStudioLogging.sendInternalError(e, fileContext.sourceFullFileName);
    }


    if (fileContext.status === "pass") {
        return true;
    } else {
        androidStudioLogging.sendTreeLocationMessage({
            kind: "WARNING", text: `Task ${transmutation.id} didn't report a successful completion`
        }, fileContext.sourceFullFileName, "WARNING");
        return false;
    }
}

async function runTransmutation(transmutation, fileContext) {

    var c = {};
    Object.assign(c, fileContext);
    delete c.status;
    c.writtenFiles = {};

    var tRunMethod = require(transmutation.sourceFile);
    await tRunMethod(c);

    fileContext.status = c.status;
    Object.assign(fileContext.writtenFiles, c.writtenFiles);

    fileContext.inputs[transmutation.id] = c.output;
    if (c.output !== undefined && !transmutation.isDependency) fileContext.lastInput = c.output;
}