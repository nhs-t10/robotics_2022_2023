module.exports = function run(context) {
    var dced = context.inputs["bc-basic-dead-code-elimination"];

    var entryBlockNames = findEntries(dced.bytecode, dced.invertedCGraph, dced.cgraph);

    entryBlockNames.forEach(x => combineBlocksFrom(x, dced.bytecode, dced.cgraph, dced.invertedCGraph));

    context.output = {
        bytecode: dced.bytecode,
        cgraph: dced.cgraph,
        invertedCGraph: dced.invertedCGraph
    };
    context.status = "pass";
}

function findEntries(bytecode, invertedCGraph, cgraph) {
    //entry blocks are blocks that don't have any jumps TO them, but DO have jumps FROM them 
    return Object.keys(bytecode).filter(x => invertedCGraph[x].length == 0 && cgraph[x].length > 0);
}

function combineBlocksFrom(entryName, bytecode, cgraph, invertedCGraph, previousBlocks) {
    //if we're going to go into unbounded recursion, exit early
    if (!previousBlocks) previousBlocks = [];
    if (previousBlocks.includes(entryName)) return;
    else previousBlocks.push(entryName);

    var to = cgraph[entryName];

    to.forEach(x => combineBlocksFrom(x, bytecode, cgraph, invertedCGraph, previousBlocks));

    if (to.length == 1) {
        var nextBlock = to[0];
        var nextFrom = invertedCGraph[nextBlock];
        if (nextFrom.length == 1 && nextFrom[0] == entryName) {

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

function replaceInInvertedCgraph(blockNames, oldLabel, newLabel, invertedCGraph) {
    for (var i = 0; i < blockNames.length; i++) {
        if (invertedCGraph[blockNames[i]].includes(oldLabel)) {
            invertedCGraph[blockNames[i]].splice(
                invertedCGraph[blockNames[i]].indexOf(oldLabel),
                1,
                newLabel
            );
        }
    }
}