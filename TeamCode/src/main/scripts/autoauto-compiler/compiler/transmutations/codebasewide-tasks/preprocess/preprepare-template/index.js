"use strict";

const templateFilename = require("./template-filename");
const { existsSync } = require("fs");
const { sendTreeLocationMessage } = require("../../../../../../script-helpers/android-studio-logging");

/**
 * @type {import("../../..").CodebaseTransmutateFunction}
 */
module.exports = async function run(context, contexts) {
    context.output = templateFilename;
    
    if(existsSync(templateFilename)) {
        context.status = "pass";
    } else {
        context.status = "fail";
        sendTreeLocationMessage({
            text: "Template doesn't exist",
            kind: "ERROR",
            original: "The template file specified with --output-java-template doesn't exist. \n" +
            "The filename given was `" + templateFilename + "`."
        });
    }
    
    
}