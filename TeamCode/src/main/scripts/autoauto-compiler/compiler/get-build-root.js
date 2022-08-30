"use strict";

const { resolve } = require("path");
const commandLineInterface = require("../../command-line-interface");

/**
 * @returns {({src:string[],gen:string,asset:string,test:string})}
 */
module.exports = function() {
    const cwd = process.cwd();
    
    return {
        src: commandLineInterface.in.map(file => resolve(cwd, file)),
        gen: resolve(cwd, commandLineInterface.out),
        asset: resolve(cwd, commandLineInterface["assets-dir"]),
        test: resolve(cwd, commandLineInterface["test-dir"])
    };
}