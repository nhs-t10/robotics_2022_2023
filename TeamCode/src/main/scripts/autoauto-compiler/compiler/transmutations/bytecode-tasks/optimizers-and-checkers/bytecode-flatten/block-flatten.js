"use strict";

/**
 * Flattens a block into an array of bytecodes
 * @param {import("../../syntax-tree-to-bytecode/ast-to-bytecode").Block} block 
 * @returns {import("../../syntax-tree-to-bytecode/ast-to-bytecode").Bytecode[]}
 */
module.exports = function(block) {
    return [].concat(
        flattenArrayOfBc(block.code),
        flattenArrayOfBc(block.jumps)
    );
}

function flattenArrayOfBc(bcs) {
    return bcs.map(x=>flattenBc(x)).flat(1);    
}

function flattenBc(bc) {
    var a = flattenArrayOfBc(bc.args);
    return a.concat([bc]);
}