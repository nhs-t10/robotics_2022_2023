"use strict";

var bytecodeSpec = require("../../bytecode-spec");

/**
 * 
 * @param {import("../../syntax-tree-to-bytecode/ast-to-bytecode").Bytecode[]} bytecode 
 * @param {Object.<string, number>} offsets 
 */
module.exports = function(bytecode, offsets) {
    const result = [];
    for(var i = 0; i < bytecode.length; i++) {
        result.push(bcLabelToIndex(bytecode[i], i, offsets));
    }
    return result;
}

function bcLabelToIndex(bc, currentIndex, offsets) {
    if (bc.code == bytecodeSpec.jmp_l.code) {
        mutOffset(bc.args[0], currentIndex, offsets);
        return Object.assign({}, bc, {code: bytecodeSpec.jmp_i.code});
    } else if (bc.code == bytecodeSpec.jmp_l_cond.code) {
        mutOffset(bc.args[1], currentIndex, offsets);
        return Object.assign({}, bc, {code: bytecodeSpec.jmp_i_cond.code});
    } else if(bc.code == bytecodeSpec.makefunction_l.code) {
        mutOffset(bc.args[0], currentIndex, offsets);
        return Object.assign({}, bc, {code: bytecodeSpec.makefunction_i.code});
    } else if(bc.code == bytecodeSpec.yieldto_l.code) {
        mutOffset(bc.args[0], currentIndex, offsets);
        return Object.assign({}, bc, {code: bytecodeSpec.yieldto_i.code});
    }
    return bc;
}

function mutOffset(instr, currentIndex, offsets) {
    instr.__value = getOffset(instr.__value, currentIndex, offsets);
}

function getOffset(lbl, currentIndex, offsets) {
    if(lbl in offsets) {        
        return (offsets[lbl] - 1) - currentIndex;
    } else {
        throw new Error("No such block '" + lbl + "'");
    }
}