"use strict";

const { workerData } = require("worker_threads");
const unitConversionHelp = require("../unit-conversion/help");

var loadArgv = require("./parse-args");
const printHelpInfo = require("./help");
var schema = require("./schema");


/**
 * @type {import("./schema").CommandLineInterface}
 */
const commandLineInterface = {};
module.exports = commandLineInterface;

loadCommandLineInterface();

testHelpFlags();

function loadCommandLineInterface() {
    if(typeof workerData === "object" && Array.isArray(workerData)) {
        process.argv = process.argv.concat(workerData);
    }
    Object.assign(commandLineInterface, loadArgv(schema));
}

function testHelpFlags() {
    if(commandLineInterface.help) {
        printHelpInfo(schema);
        process.exit();
    }
    
    if(commandLineInterface["help-detail"] === "units") {
        unitConversionHelp.printUnitsAndExit();
        process.exit();
    }
}