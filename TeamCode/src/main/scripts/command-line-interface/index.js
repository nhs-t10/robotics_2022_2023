"use strict";

const { workerData } = require("worker_threads");
const unitConversionHelp = require("../unit-conversion/help");

var loadArgv = require("./command-line-arguments");
const printHelpInfo = require("./help");
var schema = require("./schema");


/**
 * @typedef {import("./schema").CommandLineArguments} CommandLineArguments
 */


/**
 * @type CommandLineArguments
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
    }
}