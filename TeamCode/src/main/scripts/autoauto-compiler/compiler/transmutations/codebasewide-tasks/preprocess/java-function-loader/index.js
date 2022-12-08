"use strict";

var functionLoader = require("./loader");

/**
 * @type {import("../../..").CodebaseTransmutateFunction}
 */
module.exports = async function run(context, contexts) {
    context.output = await functionLoader(context.writtenFiles);
    context.status = "pass";
}