var loadArgv = require("./command-line-arguments");
const printHelpInfo = require("./help");
var schema = require("./schema");


/**
 * @typedef {import("./schema").CommandLineArguments} CommandLineArguments
 */


/**
 * @type CommandLineArguments
 */
var cla = {};

module.exports = cla;
Object.assign(cla, loadArgv(schema));


if(cla.help) {
    printHelpInfo(schema);
    process.exit();
}