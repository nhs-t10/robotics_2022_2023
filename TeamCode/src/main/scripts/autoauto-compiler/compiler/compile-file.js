const androidStudioLogging = require("../../script-helpers/android-studio-logging");


/**
 * 
 * @param {import("./transmutations").TransmutateContext} fileContext 
 * @returns {Promise<import("./worker").MaybeCompilation>}
 */
module.exports = async function (fileContext) {
    androidStudioLogging.beginOutputCapture();
    const sucStatus = await compileFile(fileContext);
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

async function tryRunTransmutation(transmutation, fileContext) {
    delete fileContext.status;

    await runTransmutation(transmutation, fileContext);

    if (fileContext.status === "pass") return true;
    else throw { kind: "ERROR", text: `Task ${transmutation.id} didn't report a successful completion` };
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

    deepFreeze(c.output);
    fileContext.inputs[transmutation.id] = c.output;
    if (c.output !== undefined && !transmutation.isDependency) fileContext.lastInput = c.output;
}

function deepFreeze(value) {
    
    if (value && typeof value === "object") { 
        for (const key of Object.getOwnPropertyNames(value)) {
            deepFreeze(value[key]);
        }
    
        return Object.freeze(value);
    } else {
        return value;
    }
}