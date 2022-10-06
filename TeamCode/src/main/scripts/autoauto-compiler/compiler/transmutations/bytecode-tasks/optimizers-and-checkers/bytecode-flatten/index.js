"use strict";

var calculateBlockLength = require("./block-length");
var replaceLabelWithIndex = require("./label-to-index");
var flattenBlock = require("./block-flatten");
var makeOrderedBlocks = require("./ordered-block-array");
const { structuredClone } = require("../../../../../../script-helpers/structured-serialise");

/**
 * 
 * @typedef {import("../../syntax-tree-to-bytecode/ast-to-bytecode").Block} Block
 * @typedef {import("../../syntax-tree-to-bytecode/ast-to-bytecode").Bytecode} Bytecode
 */

/**
 * 
 * @param {import("../../../../transmutations").TransmutateContext} context
 */
module.exports = function run(context) {
    var bytecode = structuredClone(context.lastInput);
    /** @type {Block[]} */
    var blocks = makeOrderedBlocks(bytecode);

    //calculate how long each block will be, when it's flattened
    var lengths = blocks.map(x => calculateBlockLength(x));

    //use that to calculate each block's offset
    var o = 0;
    var offsets = {};
    blocks.forEach((x,i) => {
        offsets[x.label] = o;
        o += lengths[i];
    });

    /** @type {Bytecode[]}*/
    const flatBc = [];

    //flatten each block into bytecodes, and add them to the flatBc array
    for(const b of blocks) {
        flatBc.push(...flattenBlock(b));
    }

    //replace labeled jumps with relative bytecode-number jumps
    context.output = replaceLabelWithIndex(flatBc, offsets);
    context.status = "pass";
}