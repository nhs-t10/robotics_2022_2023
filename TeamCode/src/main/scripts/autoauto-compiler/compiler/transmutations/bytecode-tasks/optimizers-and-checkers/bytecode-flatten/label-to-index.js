"use strict";

var bytecodeSpec = require("../../bytecode-spec");

/**
 * 
 * @param {import("../../syntax-tree-to-bytecode/ast-to-bytecode").Bytecode[]} bytecode 
 * @param {Object.<string, number>} offsets 
 */
module.exports = function(bytecode, offsets) {
    for(var i = 0; i < bytecode.length; i++) {
        bcLabelToIndex(bytecode[i], i, offsets);
    }
}

function bcLabelToIndex(bc, currentIndex, offsets) {
    if (bc.code == bytecodeSpec.jmp_l.code) {
        bc.code = bytecodeSpec.jmp_i.code;
        mutOffset(bc.args[0], currentIndex, offsets);
    } else if (bc.code == bytecodeSpec.jmp_l_cond.code) {
        bc.code = bytecodeSpec.jmp_i_cond.code;
        mutOffset(bc.args[1], currentIndex, offsets);
        
    } else if(bc.code == bytecodeSpec.makefunction_l.code) {
        bc.code = bytecodeSpec.makefunction_i.code;
        mutOffset(bc.args[0], currentIndex, offsets);
        
    } else if(bc.code == bytecodeSpec.yieldto_l.code) {
        bc.code = bytecodeSpec.yieldto_i.code;
        mutOffset(bc.args[0], currentIndex, offsets)
    }
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