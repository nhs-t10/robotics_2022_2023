"use strict";

const bytecodeSpec = require("../../bytecode-spec");

module.exports = function run(context) {
    var dced = context.inputs["bc-basic-dead-code-elimination"];

    var entryBlockNames = findBlocksWithoutParent(dced.bytecode, dced.invertedCGraph, dced.cgraph);

    entryBlockNames.forEach(x => combineBlocksFrom(x, dced.bytecode, dced.cgraph, dced.invertedCGraph));

    context.output = {
        bytecode: dced.bytecode,
        cgraph: dced.cgraph,
        invertedCGraph: dced.invertedCGraph
    };
    context.status = "pass";
}

function findBlocksWithoutParent(bytecode, invertedCGraph, cgraph) {
    //entry blocks are blocks that don't have any jumps TO them, but DO have jumps FROM them 
    return Object.keys(bytecode).filter(x => invertedCGraph[x].length == 0 && cgraph[x].length > 0);
}

function combineBlocksFrom(entryName, bytecode, cgraph, invertedCGraph, previousBlocks) {
    //if we're going to go into unbounded recursion, exit early
    if (!previousBlocks) previousBlocks = [];
    if (previousBlocks.includes(entryName)) return;
    else previousBlocks.push(entryName);

    var to = cgraph[entryName];

    to.forEach(x => combineBlocksFrom(x.label, bytecode, cgraph, invertedCGraph, previousBlocks));

    if (to.length == 1 && to[0].type != bytecodeSpec.makefunction_l.code) {
        var nextBlock = to[0].label;
        var nextFrom = invertedCGraph[nextBlock];
        
        if (nextFrom.length == 1 && nextFrom[0].label == entryName) {

            bytecode[entryName].code = bytecode[entryName].code.concat(bytecode[nextBlock].code);
            bytecode[entryName].jumps = bytecode[nextBlock].jumps;

            cgraph[entryName] = cgraph[nextBlock];
            delete cgraph[nextBlock];
            delete bytecode[nextBlock];

            delete invertedCGraph[nextBlock];
            replaceInInvertedCgraph(cgraph[entryName], nextBlock, entryName, invertedCGraph);
        }
    }
}

/**
 * Reorganizes the inverted control graph when blocks are combined.
 * 
 * When blocks like this:
 * 
 * a -> b -> c
 * (a jumps to b, which jumps to c)
 * 
 * change to this:
 * 
 * a (& b) -> c
 * 
 * then c's ICG must be updated. That's what this function is for.
 * 
 * @param {({label:string})[]} blockNames Array of labels which require renaming. The 'c's in the example.
 * @param {string} oldLabel Labels to rename from. The 'b's in the example
 * @param {string} newLabel Labels to rename to. The 'a's in the example
 * @param {*} invertedCGraph The inverted control graph
 */
function replaceInInvertedCgraph(blockNames, oldLabel, newLabel, invertedCGraph) {
    for (var i = 0; i < blockNames.length; i++) {
        const lbl = blockNames[i].label;
        
        const index = invertedCGraph[lbl].findIndex(x => x.label == oldLabel);
            
        if (index != -1) {
            invertedCGraph[lbl][index].label = newLabel;
        }
    }
}