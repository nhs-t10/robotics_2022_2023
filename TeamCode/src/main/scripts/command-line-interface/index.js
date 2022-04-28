var loadArgv = require("./command-line-arguments");
var schema = require("./schema");


/**
 * @typedef {import("./schema").CommandLineArguments} CommandLineArguments
 */


/**
 * @type CommandLineArguments
 */
var cla = {};

module.exports = cla;

loadArgv(schema);

Object.assign(cla, schema);