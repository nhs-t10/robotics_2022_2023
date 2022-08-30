"use strict";

const { normalize } = require("path");

/**
 * @typedef {object} CommandLineArguments
 * @property {boolean} ascii
 * @property {boolean} color
 * @property {boolean} quiet
 * @property {boolean} no-cache
 * @property {boolean} no-cache-save
 * @property {number} threads
 * @property {boolean} progress
 * @property {boolean} agpbi
 * @property {boolean} build-history
 * @property {boolean} help
 * @property {boolean} debug
 * @property {"units" | ""} help-detail
 * @property {boolean} no-appendix
 * @property {string[]} in
 * @property {string} out
 * @property {string} assets-dir
 * @property {string} test-dir
 */


module.exports = {
    progress: {
        value: false,
        short: ["p"],
        description: "Prints progress of compiler as it goes"
    },
    quiet: {
        value: false,
        short: ["q"],
        description: "Suppresses warnings and info-messages"
    },
    ascii: {
        value: false,
        short: ["a"],
        description: "Only use ASCII in printouts"
    },
    color: {
        value: true,
        short: ["c"],
        description: "Color-code the output. Default: true"
    },
    "no-cache": {
        value: false,
        short: [],
        description: "Ignore cached calculations from previous compiles"
    },
    "no-cache-save": {
        value: false,
        short: [],
        description: "Don't save calculations to the cache"
    },
    threads: {
        value: require("os").cpus().length,
        short: [],
        description: "How many threads to use when compiling"
    },
    "agpbi": {
        value: false,
        short: [],
        description: "Print Android Studio's JSON message format"
    },
    "build-history": {
        value: false,
        short: [],
        description: "Give each build a name and record it in the Geneology"
    },
    help: {
        value: false,
        short: ["h"],
        description: "Print help message and exit"
    },
    debug: {
        value: false,
        short: [],
        description: "Print detailed debug information"
    },
    "help-detail": {
        value: "",
        short: [],
        description: "Print detailed help information and exit. Use 'units' to specify the part that you want help for."
    },
    "no-appendix": {
        value: false,
        short: [],
        description: "Hide detailed type outlines."
    },
    "make-tests": {
        value: false,
        short: [],
        description: "Generate tests in the __testedautoauto directory"
    },
    "in": {
        default: [process.cwd()],
        value: [],
        short: [],
        description: "The directory(s) to find Autoauto files from"
    },
    "out": {
        value: process.cwd(),
        short: [],
        description: "The directory to write generated files into"
    },
    "assets-dir": {
        value: process.cwd(),
        short: [],
        description: "The directory to write assets into"
    },
    "test-dir": {
        value: process.cwd(),
        short: [],
        description: "The directory to write tests into"
    }
}