module.exports = function (context) {
    var bytecode = context.inputs["bc-condense-constants"];
    var cgraph = context.inputs["build-cgraph"];
    var invertedCGraph = context.inputs["inverted-cgraph"];

    var memoReachability = {};

    Object.keys(bytecode).forEach(x => {
        removeBlockIfDead(x, bytecode, cgraph, invertedCGraph, memoReachability);
    });

    cleanCgraphs(bytecode, cgraph, invertedCGraph);

    context.output = {
        bytecode: bytecode,
        cgraph: cgraph,
        invertedCGraph: invertedCGraph
    };
    context.status = "pass";

}

function removeBlockIfDead(blockLabel, bytecode, cgraph, invertedCgraph, memoReachability) {

    if (!isReachable(blockLabel, invertedCgraph, memoReachability)) {
        delete bytecode[blockLabel];
    }
}

function cleanCgraphs(bytecode, cgraph, invertedCGraph) {
    Object.keys(invertedCGraph).forEach(x => {
        if (!bytecode[x]) delete invertedCGraph[x];
        else invertedCGraph[x] = invertedCGraph[x].filter(y => bytecode[y] != undefined);
    });
    Object.keys(cgraph).forEach(x => {
        if (!bytecode[x]) delete cgraph[x];
        else cgraph[x] = cgraph[x].filter(y => bytecode[y] != undefined);

    });
}

function isReachable(blockLabel, invertedCgraph, memoReachability, previouslyScannedBlocks) {
    if (memoReachability.hasOwnProperty(blockLabel)) return memoReachability[blockLabel];

    if (isDynamicallyReferenced(blockLabel)) return true;
    if (invertedCgraph[blockLabel].length == 0) return false;

    //infinite recursion checks
    if (previouslyScannedBlocks === undefined) previouslyScannedBlocks = [];
    if (previouslyScannedBlocks.includes(blockLabel)) return undefined;
    //put it on the start instead of the end. This makes `includes()` quicker, on average.
    previouslyScannedBlocks.unshift(blockLabel);
    //clone it so that the recursion won't modify other branches
    previouslyScannedBlocks = previouslyScannedBlocks.slice()

    var cacheSaveValid = true;
    var parents = invertedCgraph[blockLabel];
    for (var i = 0; i < parents.length; i++) {
        var pReach = isReachable(parents[i], invertedCgraph, memoReachability, previouslyScannedBlocks);
        if (pReach) {
            return memoReachability[blockLabel] = true;
        }
        else if (pReach === undefined) {
            cacheSaveValid = false;
        }
    }

    if (cacheSaveValid) {
        return memoReachability[blockLabel] = false;
    } else {
        return undefined;
    }
}

function isDynamicallyReferenced(blockLabel) {
    return blockLabel == "ENTRY" || blockLabel.includes("func_enter");
}