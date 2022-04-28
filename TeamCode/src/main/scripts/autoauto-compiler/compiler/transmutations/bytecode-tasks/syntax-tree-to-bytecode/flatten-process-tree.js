var cPool = require("./constant-pool");

var treeBlockToBytecodeBlock = require("./ast-to-bytecode");
const bytecodeSpec = require("../bytecode-spec");

module.exports = function(ast) {
    var constantPool = cPool();
    var treeBlocks = programToTreeBlocks(ast);
    
    var bytecodeBlocks = treeBlocks.map(x=>treeBlockToBytecodeBlock(x, constantPool));
    
    var flattedBlocks = bytecodeBlocks.flat(1);
    
    var blockRecords = Object.fromEntries(flattedBlocks.map(x=>[x.label, x]));
    
    if(blockRecords["ENTRY"]) {
        throw "entry block defined!"
    } else {
        blockRecords["ENTRY"] = {
            label: "ENTRY",
            code: [],
            jumps: [{
                code: bytecodeSpec.jmp_l.code,
                location: undefined,
                args: [{ code: constantPool.getCodeFor(treeBlocks[0].label), __value: treeBlocks[0].label, args: [] }]
            }]
        };
    }
    
    return {
        blocks: blockRecords
    };
}

function programToTreeBlocks(program) {
    var blocks = program.statepaths.map(x=>
        x.statepath.states.map((y,i,a)=>({
            label: "s/" + x.label + "/" + i,
            treeStatements: y.statement,
            stateCountInPath: a.length
        }))
    ).flat();
    return blocks;   
}