"use strict";

/**
 * Calculates the total amount of bytecodes in a block.
 * @param {import("../../syntax-tree-to-bytecode/ast-to-bytecode").Block} block 
 * @returns {number}
 */
module.exports = function(block) {
    return bcArrayLength(block.code) + bcArrayLength(block.jumps);
}

function bcArrayLength(bcArray) {
    var t = 0;
    for(var i = 0; i < bcArray.length; i++) t += bytecodeLength(bcArray[i]);
    return t;
}

function bytecodeLength(bc) {
    return bcArrayLength(bc.args) + 1;
}