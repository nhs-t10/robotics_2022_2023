const androidStudioLogging = require("../../script-helpers/android-studio-logging");


module.exports = async function(fileContext) {
    androidStudioLogging.beginOutputCapture();
    var success = await compileFile(fileContext);
    var log = androidStudioLogging.getCapturedOutput();

    return {
        success: success,
        fileContext: fileContext,
        log: log
    }
}

/**
 * 
 * @param {import("./transmutations").TransmutateContext} fileContext 
 */
async function compileFile(fileContext) {
    var transmuts = fileContext.transmutations;
    for(var i = 0; i < transmuts.length; i++) {
        
        var mutRan = await tryRunTransmutation(transmuts[i], fileContext);
        
        if(!mutRan) break;
    }
    return i == transmuts.length;
}

async function tryRunTransmutation(transmutation, fileContext) {
    try {
        delete fileContext.status;
        
        await runTransmutation(transmutation, fileContext);
        
        if(fileContext.status != "pass") throw {kind: "ERROR", text: `Task ${transmutation.id} didn't report success` };
        
        return true;
    } catch(e) {
        fileContext.status = "fail";
        
        if(e instanceof Error) {
            androidStudioLogging.sendTreeLocationMessage({
                kind: "ERROR",
                text: "Internal Compiler Error",
                original: `There was an internal error. This file will be skipped, but others will still be compiled.\n` + 
                `Please contact these people in this order: \n` +
                `1) Connor\n` +
                `2) Chloe\n` +
                `\n` +
                `The stack of the error is below:\n` +
                e.message + "\n" + e.stack
            });
        } else {
            androidStudioLogging.sendTreeLocationMessage(e, fileContext.sourceFullFileName, "ERROR");
        }
        
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
    if(c.output !== undefined && !transmutation.isDependency) fileContext.lastInput = c.output;
}