var bytecodeSpec = require("../../bytecode-spec");

module.exports = function(block, bytecodeBlocks) {
    bcArrLabelToIndex(block.code, bytecodeBlocks);
    bcArrLabelToIndex(block.jumps, bytecodeBlocks);
}

function bcArrLabelToIndex(bcArr, bytecodeBlocks) {
    bcArr.forEach(x=>bcLabelToIndex(x, bytecodeBlocks));
}

function bcLabelToIndex(bc, bytecodeBlocks) {
    bcArrLabelToIndex(bc.args, bytecodeBlocks);
    
    if (bc.code == bytecodeSpec.jmp_l.code) {
        bc.code = bytecodeSpec.jmp_i.code;
        mutOffset(bc.args[0], bytecodeBlocks);
        
    } else if (bc.code == bytecodeSpec.jmp_l_cond.code) {
        bc.code = bytecodeSpec.jmp_i_cond.code;
        mutOffset(bc.args[1], bytecodeBlocks);
        
    } else if(bc.code == bytecodeSpec.makefunction_l.code) {
        bc.code = bytecodeSpec.makefunction_i.code;
        mutOffset(bc.args[0], bytecodeBlocks);
        
    } else if(bc.code == bytecodeSpec.yieldto_l.code) {
        bc.code = bytecodeSpec.yieldto_i.code;
        mutOffset(bc.args[0], bytecodeBlocks)
    }
}

function mutOffset(instr, bytecodeBlocks) {
    instr.__value = getOffset(instr.__value, bytecodeBlocks);
}

function getOffset(lbl, bytecodeBlocks) {
    if(!bytecodeBlocks[lbl]) {
        throw "No such block '" + lbl + "'";
    } else {
        return bytecodeBlocks[lbl].offset;
    }
}