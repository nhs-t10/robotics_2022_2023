"use strict";

const { sha } = require("../../../../../../script-helpers/sha-string");
const bytecodeSpec = require("../../bytecode-spec");

module.exports = function run(context) {
    var bb = context.inputs["bc-basic-dead-code-elimination"];
    var bytecode = bb.bytecode;
    var cgraph = bb.cgraph;

    var invertedCgraph = bb.invertedCGraph;

    var globalVarnameCounters = { blocks: {}, vars: {} };

    var rootBlocks = getBlocksWithoutParents(bytecode, invertedCgraph);

    rootBlocks.forEach(x => ssaBlock(x, bytecode, cgraph, globalVarnameCounters, ""));
    rootBlocks.forEach(x => copyLastSetToChildrenRecursiveNetwork(x.label, cgraph, globalVarnameCounters));

    Object.values(bytecode).forEach(x => insertPhiNodes(x, invertedCgraph, globalVarnameCounters));

    context.output = bytecode;
    context.status = "pass";
}

function getBlocksWithoutParents(bytecode, invertedCgraph) {
    return Object.values(bytecode).filter(x => invertedCgraph[x.label].length == 0);
}

function insertPhiNodes(block, invertedCgraph, globalVarnameCounters) {

    var varsGotten = globalVarnameCounters.blocks[block.label].firstreads;
    for (var k in varsGotten) {
        var parentSets = getAllParentSetsOfVariable(k, block.label, invertedCgraph[block.label], globalVarnameCounters);

        if (parentSets.length == 0) parentSets = k + "@0";
        else if (parentSets.length == 1) parentSets = parentSets[0];
        else parentSets = { __phi: parentSets };

        varsGotten[k].forEach(x => x.__value = parentSets);
    }

}

function ssaBlock(block, bytecode, cgraph, globalVarnameCounters, rootBlockPrefix) {

    if (block.label.includes("func-enter")) rootBlockPrefix = block.label + "|arg:";

    initVariableCounter(block, globalVarnameCounters);

    if (hasBeenSsadAlready(block, globalVarnameCounters)) return;
    markAsSsad(block, globalVarnameCounters);

    ssaBytecodeArray(block.code, block.label, globalVarnameCounters, rootBlockPrefix);
    ssaBytecodeArray(block.jumps, block.label, globalVarnameCounters, rootBlockPrefix);

    ensureChildrenAreSsad(block, bytecode, cgraph, globalVarnameCounters, rootBlockPrefix);
}


function ssaBytecodeArray(bcArr, blockLabel, globalVarnameCounters, rootBlockPrefix) {
    bcArr.forEach(x => {
        ssaBytecodeInstruction(x, blockLabel, globalVarnameCounters, rootBlockPrefix);
    });
}

function ssaBytecodeInstruction(instr, blockLabel, globalVarnameCounters, rootBlockPrefix) {
    ssaBytecodeArray(instr.args, blockLabel, globalVarnameCounters, rootBlockPrefix);

    if (isVariableAddressingInstr(instr)) {
        var varInstr = findVarnameInstructionFromInstr(instr);

        const plainVariableName = varInstr.__value;
        const variableName = getShadowedVariableName(plainVariableName, globalVarnameCounters, rootBlockPrefix);

        if (isVariableSettingInstr(instr)) incrementSingleStaticVariable(blockLabel, variableName, globalVarnameCounters);

        varInstr.__value = getSingleStaticVariableName(blockLabel, variableName, varInstr, globalVarnameCounters);
    } else if (isFuncDefInstr(instr)) {
        assignFunctionArgumentNames(instr, globalVarnameCounters, rootBlockPrefix);
    }
}

function getShadowedVariableName(plainVariableName, globalVarnameCounters, rootBlockPrefix) {
    
    //if it's defined as an argument, prefix it. Otherwise, just use the plain version.
    if (globalVarnameCounters.definedAsArgument &&
        globalVarnameCounters.definedAsArgument[rootBlockPrefix + plainVariableName]) {
            return rootBlockPrefix + plainVariableName;
    } else {
        return plainVariableName;
    }
}

function incrementSingleStaticVariable(blockLabel, plainVariableName, globalVarnameCounters) {
    var variableRecord = getVariableSSARecord(blockLabel, plainVariableName, globalVarnameCounters);

    globalVarnameCounters.vars[plainVariableName]++;
    variableRecord.blockScopeCounter++;
    variableRecord.varname = plainVariableName + "@" + globalVarnameCounters.vars[plainVariableName];

    setLastSetNameForLaterPhi(blockLabel, plainVariableName, variableRecord.varname, globalVarnameCounters);
}

function assignFunctionArgumentNames(makefunctionInstr, globalVarnameCounters, rootBlockPrefix) {
    var lbl = makefunctionInstr.args[0].__value;
    if (globalVarnameCounters.argumentNames == undefined) globalVarnameCounters.argumentNames = {};
    if (globalVarnameCounters.argumentNames[lbl] == undefined) globalVarnameCounters.argumentNames[lbl] = [];

    if (globalVarnameCounters.definedAsArgument == undefined) globalVarnameCounters.definedAsArgument = {};

    const filePrefix = sha(makefunctionInstr.location.file) + "-";
    
    for (var i = 1; i < makefunctionInstr.args.length - 1; i += 2) {
        const argname = makefunctionInstr.args[i].__value;
        const variableReferringToArg = filePrefix + argname;
        globalVarnameCounters.argumentNames[lbl].push(variableReferringToArg);

        globalVarnameCounters.definedAsArgument[lbl + "|arg:" + variableReferringToArg] = true;
    }
}

function getSingleStaticVariableName(blockLabel, plainVariableName, instruction, globalVarnameCounters) {
    var variableRecord = getVariableSSARecord(blockLabel, plainVariableName, globalVarnameCounters);

    if (variableRecord.blockScopeCounter == 0) {
        setFirstReadInstructionForLaterPhi(blockLabel, plainVariableName, instruction, globalVarnameCounters);
    }
    return variableRecord.varname;
}

function copyLastSetToChildrenRecursiveNetwork(blockLabel, cgraph, globalVarnameCounters) {
    //recursion protection
    if (globalVarnameCounters.blocks[blockLabel].hasPhiCopied) return;
    globalVarnameCounters.blocks[blockLabel].hasPhiCopied = true;

    copyLastSetInfoToChildren(blockLabel, cgraph, globalVarnameCounters);

    var children = cgraph[blockLabel];
    children.forEach(x => {
        copyLastSetToChildrenRecursiveNetwork(x.label, cgraph, globalVarnameCounters);
    });
}

function copyLastSetInfoToChildren(blockLabel, cgraph, globalVarnameCounters) {
    if (!globalVarnameCounters.blocks[blockLabel].lastsets) {
        globalVarnameCounters.blocks[blockLabel].lastsets = {};
    }

    var thisLastsets = globalVarnameCounters.blocks[blockLabel].lastsets;

    var children = cgraph[blockLabel];

    if (globalVarnameCounters.argumentNames &&
        globalVarnameCounters.argumentNames[blockLabel]) {
        var argnames = globalVarnameCounters.argumentNames[blockLabel];

        for (var i = 0; i < children.length; i++) {
            globalVarnameCounters.argumentNames[children[i].label] = argnames;
        }
    }

    for (var k in thisLastsets) {
        for (var i = 0; i < children.length; i++) {
            if (!globalVarnameCounters.blocks[children[i].label].lastsets) globalVarnameCounters.blocks[children[i].label].lastsets = {};

            var childLastsets = globalVarnameCounters.blocks[children[i].label].lastsets;
            if (!childLastsets[k]) childLastsets[k] = [];
            uniquelyPush(childLastsets[k], thisLastsets[k]);
        }
    }


}

function getAllParentSetsOfVariable(variable, thisBlockLabel, parentLabels, globalVarnameCounters) {
    var sets = [];

    if (globalVarnameCounters.argumentNames &&
        globalVarnameCounters.argumentNames[thisBlockLabel]) {
        var argnames = globalVarnameCounters.argumentNames[thisBlockLabel];
        if (argnames.includes(variable)) {
            sets.push(thisBlockLabel + "|arg:" + variable);
        }
    }

    for (var i = 0; i < parentLabels.length; i++) {
        uniquelyPush(sets, globalVarnameCounters.blocks[parentLabels[i].label].lastsets[variable] || []);
    }
    return sets;
}

function setLastSetNameForLaterPhi(blockLabel, plainVariableName, vName, globalVarnameCounters) {
    if (!globalVarnameCounters.blocks[blockLabel].lastsets) {
        globalVarnameCounters.blocks[blockLabel].lastsets = {};
    }

    globalVarnameCounters.blocks[blockLabel].lastsets[plainVariableName] = [vName];
}

function setFirstReadInstructionForLaterPhi(blockLabel, plainVariableName, instruction, globalVarnameCounters) {
    if (!globalVarnameCounters.blocks[blockLabel].firstreads) {
        globalVarnameCounters.blocks[blockLabel].firstreads = {};
    }
    if (!globalVarnameCounters.blocks[blockLabel].firstreads[plainVariableName]) {
        globalVarnameCounters.blocks[blockLabel].firstreads[plainVariableName] = [];
    }

    globalVarnameCounters.blocks[blockLabel].firstreads[plainVariableName].push(instruction);
}

function getVariableSSARecord(blockLabel, plainVariableName, globalVarnameCounters) {
    var vc = globalVarnameCounters.blocks[blockLabel].vars;

    if (!globalVarnameCounters.vars[plainVariableName]) globalVarnameCounters.vars[plainVariableName] = 0;

    if (!vc[plainVariableName]) vc[plainVariableName] = {
        plain: plainVariableName,
        blockScopeCounter: 0,
        varname: plainVariableName + "@0"
    };

    return vc[plainVariableName];
}

function initVariableCounter(block, globalVarnameCounters) {
    if (!globalVarnameCounters.blocks[block.label]) {
        globalVarnameCounters.blocks[block.label] = {
            ssa: false,
            vars: {}
        };
    }
}

function hasBeenSsadAlready(block, globalVarnameCounters) {
    return globalVarnameCounters.blocks[block.label].ssa;
}

function markAsSsad(block, globalVarnameCounters) {
    globalVarnameCounters.blocks[block.label].ssa = true;
}

function ensureChildrenAreSsad(block, bytecode, cgraph, globalVarnameCounters, rootBlockPrefix) {
    var children = cgraph[block.label] || [];

    children.forEach(x => ssaBlock(bytecode[x.label], bytecode, cgraph, globalVarnameCounters, rootBlockPrefix));
}

function findVarnameInstructionFromInstr(instr) {
    var code = instr.code;
    if (code == bytecodeSpec.setvar.code || code == bytecodeSpec.getvar.code) {
        return instr.args[0];
    }
    else {
        throw {
            text: "Malformed bytecode: unable to derive variable name from variable-addressing bytecode!",
            location: instr.location
        };
    }
}

function isVariableAddressingInstr(instr) {
    return instr.code == bytecodeSpec.setvar.code || instr.code == bytecodeSpec.getvar.code;
}

function isFuncDefInstr(instr) {
    return instr.code == bytecodeSpec.makefunction_l.code;
}

function isVariableSettingInstr(instr) {
    return instr.code == bytecodeSpec.setvar.code;
}


function uniqueValues(arr) {
    return Array.from(new Set(arr));
}

function uniquelyPush(arr, valuesToPush) {
    for (var i = 0; i < valuesToPush.length; i++) {
        if (!arr.includes(valuesToPush[i])) arr.push(valuesToPush[i]);
    }
}