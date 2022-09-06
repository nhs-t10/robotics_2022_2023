"use strict";

const { createHash } = require("crypto");
const path = require("path");
const systemVariableNames = require("../../../data/system-variable-names");
const { requestDependencyToParent } = require("../../../worker");
const bytecodeSpec = require("../bytecode-spec");
const { emitBytecodeWithLocation, emitConstantWithLocation, jumpToLabel } = require("./generation-utils");

const { STATE_INIT_PREFIX, PROGRAM_INIT_PREFIX } = require("./prefixes.js");

module.exports = treeBlockToBytecode;

/**
 * 
 * @param {import("./flatten-process-tree").TreeBlock} block 
 * @param {import("../constant-pool").constantpool} constantPool
 * @param {Object.<string, *} frontmatter 
 * @returns {Block[]}
 */
async function treeBlockToBytecode(block, constantPool, frontmatter) {

    var statementLabels = block.treeStatements.map((x, i) => block.label + "/stmt/" + i);

    var stateStartBlock = {
        label: block.label,
        code: [],
        jumps: [jumpToLabel(statementLabels[0], constantPool)]
    };

    var stateEndBlock = {
        label: block.label + "/end",
        code: [],
        jumps: [emitBytecodeWithLocation(bytecodeSpec.yieldto_l, [
            emitConstantWithLocation(statementLabels[0], constantPool, {})
        ], {}, constantPool.currentFile)]
    }

    var blocks = [];

    for (var i = 0; i < block.treeStatements.length; i++) {
        var thisLabel = statementLabels[i];
        var nextLabel = statementLabels[i + 1] || stateEndBlock.label;

        var stmtBlocks = await statementToBytecodeBlocks(block.treeStatements[i], thisLabel, constantPool, nextLabel, block.stateCountInPath);

        blocks = blocks.concat(stmtBlocks);
    }

    if (frontmatter.noStopBetweenStates == false) {
        blocks.push(makeInitBlockToStopMotors(constantPool));
    }

    findAndRewriteStateInitBlocks(stateStartBlock, blocks, constantPool);

    return blocks.concat([stateStartBlock, stateEndBlock]);
}

/**
 * 
 * @param {Block} startBlock 
 * @param {Block[]} allStatementBlocks
 */
function findAndRewriteStateInitBlocks(startBlock, allStatementBlocks, constantPool) {
    var stateInitBlocks = allStatementBlocks.filter(x => x.label.startsWith(STATE_INIT_PREFIX));

    if (stateInitBlocks.length == 0) return;

    //make the last state init block go to wherever the start block was going
    stateInitBlocks[stateInitBlocks.length - 1].jumps = startBlock.jumps;
    //make the start block go to the first state init block
    startBlock.jumps = [jumpToLabel(stateInitBlocks[0].label, constantPool)];

    for (var i = 0; i < stateInitBlocks.length - 1; i++) {
        stateInitBlocks[i].jumps = [
            jumpToLabel(stateInitBlocks[i + 1].label, constantPool)
        ];
    }
}

function makeInitBlockToStopMotors(constantPool) {
    return makeStateinitBlock([
        emitBytecodeWithLocation(bytecodeSpec.callfunction, [
            emitBytecodeWithLocation(bytecodeSpec.getvar, [
                emitConstantWithLocation("stopDrive", constantPool)
            ]),
            emitConstantWithLocation(0, constantPool),
            emitConstantWithLocation(0, constantPool)
        ], {})
    ], constantPool);
}

/**
 * @typedef {object} Block
 * @property {string} label
 * @property {Bytecode[]} code
 * @property {Bytecode[]} jumps
 */

/**
 * 
 * @param {*} ast 
 * @param {string} label
 * @param {import("../constant-pool").constantpool} constantPool 
 * @param {string} afterThisJumpToLabel 
 * @returns {Promise<Block[]>}
 */
async function statementToBytecodeBlocks(ast, label, constantPool, afterThisJumpToLabel, stateCountInPath) {

    switch (ast.type) {
        case "SkipStatement":
            return skipStatementToBytecode(ast, label, constantPool, stateCountInPath);
        case "NextStatement":
            return nextStatementToBytecode(ast, label, constantPool, stateCountInPath);
        case "LetPropertyStatement":
            return await letPropertyToBytecode(ast, label, constantPool, afterThisJumpToLabel);
        case "FunctionDefStatement":
            return await functionDefToBytecode(ast, label, constantPool, afterThisJumpToLabel);
        case "Block":
            return compoundStatementToBytecode(ast, label, constantPool, afterThisJumpToLabel, stateCountInPath);
        case "PassStatement":
            return passStatementToBytecode(ast, label, constantPool, afterThisJumpToLabel);
        case "IfStatement":
            return await ifStatementToBytecode(ast, label, constantPool, afterThisJumpToLabel, stateCountInPath);
        case "LetStatement":
            return await letStatementToBytecode(ast, label, constantPool, afterThisJumpToLabel);
        case "GotoStatement":
            return await gotoStatementToBytecode(ast, label, constantPool);
        case "ReturnStatement":
            return await returnStatementToBytecode(ast, label, constantPool);
        case "NextStatement":
            return nextStatementToBytecode(ast, label, constantPool, stateCountInPath);
        case "AfterStatement":
            return await afterStatementToBytecode(ast, label, constantPool, afterThisJumpToLabel, stateCountInPath);
        case "ValueStatement":
            return await valueStatementToBytecode(ast, label, constantPool, afterThisJumpToLabel);
        case "ProvideStatement":
            return await provideStatementToBytecode(ast, label, constantPool, afterThisJumpToLabel);
        default:
            console.error(ast);
            throw new Error("Can't convert " + ast.type + " to bytecode!");

    }
}

/**
 * @typedef {object} valueComputationBytecode
 * @property {Block[]} dependentBlocks
 * @property {Bytecode} computation
 */

/**
 * 
 * @param {*} valueAst 
 * @param {*} constantPool 
 * @returns {Promise<valueComputationBytecode>}
 */
async function valueToBytecodeBlocks(valueAst, constantPool) {
    var simpleBytecode = primitiveValueToSimpleBytecode(valueAst, constantPool);

    //if it was able to make that into a simple bytecode array, great!
    if (simpleBytecode !== undefined) {
        return {
            dependentBlocks: [],
            computation: simpleBytecode
        };
    }

    //otherwise, try the more-complex ones.
    switch (valueAst.type) {
        case "FunctionLiteral":
            return await functionLiteralToBytecode(valueAst, constantPool)
        case "OperatorExpression":
        case "ComparisonOperator":
            return await operationToBytecode(valueAst, constantPool);
        case "TitledArgument":
            return await titledArgToBytecode(valueAst, constantPool);
        case "ArgumentList":
            throw "Must call argumentListToBytecode directly, as it returns an array of bytecodes!";
        case "DynamicValue":
            return await valueToBytecodeBlocks(valueAst.value, constantPool);
        case "ArrayLiteral":
            return await tableLiteralToBytecode(valueAst, constantPool);
        case "FunctionCall":
            return await functionCallToBytecode(valueAst, constantPool);
        case "TailedValue":
            return await tailedValueToBytecode(valueAst, constantPool);
        case "DelegatorExpression":
            return await delegatorExpressionToBytecode(valueAst, constantPool);
    }
    throw new Error("Unknown value type " + valueAst.type);
}

/**
 *
 * @param {*} valueAst
 * @param {import("../constant-pool").constantpool} constantPool
 * @returns {Promise<valueComputationBytecode>}
 */
async function delegatorExpressionToBytecode(ast, constantPool) {
    const currentFile = constantPool.currentFile;
    const currentFolder = path.dirname(currentFile);

    if (ast.delegateTo.str === undefined) throw {
        text: "Sanity check failed -- a malformed delegate() expression exists!",
        hints: ["Make sure that the first argument to delegate() is a string literal"],
        location: ast.location,
        kind: "ERROR"
    }
    let dependnecyString = ast.delegateTo.str;
    if (!dependnecyString.endsWith(".autoauto")) dependnecyString += ".autoauto";

    const dependencyFile = path.join(currentFolder, dependnecyString);


    if (dependencyFile in constantPool.dependencyLabels) {
        console.log(constantPool.dependencyLabels)
        return emitExpressionOfPreviousDependency(constantPool, dependencyFile, ast);
    } else {
        const dependencyCode = await createDependencyBlocks(currentFile, dependencyFile, constantPool, ast);
        constantPool.dependencyLabels[dependencyFile] = dependencyCode.entryLabel;
        return emitExpressionOfDependencyFile(dependencyCode, constantPool, ast);
    }
}

/**
 * 
 * @param {DependencyCodeRecord} dependencyCode 
 * @param {import("../constant-pool").constantpool} constantPool 
 * @param {import("../../text-to-syntax-tree/parser").AutoautoASTElement} ast 
 * @returns {valueComputationBytecode}
 */
function emitExpressionOfDependencyFile(dependencyCode, constantPool, ast) {
    return emitFunctionCallToLabel(dependencyCode.entryLabel, constantPool, ast, dependencyCode.blocks);
}

/**
 * 
 * @param {string} label
 * @param {import("../constant-pool").constantpool} constantPool 
 * @param {import("../../text-to-syntax-tree/parser").AutoautoASTElement} ast 
 * @param {Block[]?} dependentBlocks 
 */
function emitFunctionCallToLabel(label, constantPool, ast, dependentBlocks) {
    return {
        dependentBlocks: dependentBlocks || [],
        computation: emitBytecodeWithLocation(bytecodeSpec.callfunction, [
            emitBytecodeWithLocation(bytecodeSpec.makefunction_l, [
                emitConstantWithLocation(label, constantPool, ast),
                emitConstantWithLocation(0, constantPool, ast)
            ], ast),
            emitConstantWithLocation(0, constantPool, ast),
            emitConstantWithLocation(0, constantPool, ast)
        ], ast)
    };
}

/**
 * 
 * @param {import("../constant-pool").constantpool} constantPool 
 * @param {string} dependencyFile 
 * @param {import("../../text-to-syntax-tree/parser").AutoautoASTElement} ast
 * @returns {valueComputationBytecode}
 */
function emitExpressionOfPreviousDependency(constantPool, dependencyFile, ast) {
    const label = constantPool.dependencyLabels[dependencyFile];
    return emitFunctionCallToLabel(label, constantPool, ast, []);
}

/**
 * @typedef {object} DependencyCodeRecord
 * @property {Block[]} blocks
 * @property {string} entryLabel
 */

/**
 * @param {string} currentFile
 * @param {string} dependencyFile 
 * @param {import("../constant-pool").constantpool} constantPool
 * @param {import("../../text-to-syntax-tree/parser").AutoautoASTElement} ast
 * 
 * @returns {Promise<DependencyCodeRecord>}
 */
async function createDependencyBlocks(currentFile, dependencyFile, constantPool, ast) {
    const dependency = await requestDependencyToParent(currentFile, dependencyFile);

    const dependencyPrefix = sha(dependency.sourceFullFileName);
    const depBytecode = dependency.inputs["syntax-tree-to-bytecode"].blocks;

    const dependencyBlocks = Object.values(depBytecode);

    addFileToLocations(dependencyBlocks, ast.location.file);

    //rename the ENTRY block to a non-overlapping label
    const realEntryLabel = "sl/" + dependencyPrefix + "/real-entry";
    depBytecode.ENTRY.label = realEntryLabel;



    //make a continuation block that has the information required to do a jump
    const continuationEntry = constructDelegatorContinuationBranch(dependencyBlocks, constantPool, ast, realEntryLabel, dependencyPrefix);
    const continuationEntryLabel = continuationEntry.label;
    dependencyBlocks.push(continuationEntry);

    //construct the module_args variable
    const modArgsName = dependencyPrefix + "-" + systemVariableNames.MODULE_ARGS;
    const modArgsBytecode = await argumentListToBytecodeAsProperties(ast.args, constantPool);
    const modArgs = emitBytecodeWithLocation(bytecodeSpec.setvar, [
        emitConstantWithLocation(modArgsName, constantPool, ast),
        emitBytecodeWithLocation(bytecodeSpec.construct_table, modArgsBytecode.boundedCalculations, ast)
    ], {}, dependency.sourceFullFileName);
    const entryLabel = constantPool.subblockLabel("func-enter");
    const modArgsSetter = {
        label: entryLabel,
        code: [modArgs],
        jumps: [jumpToLabel(continuationEntryLabel, constantPool)]
    }
    dependencyBlocks.push(modArgsSetter);

    return {
        blocks: dependencyBlocks.concat(modArgsBytecode.dependentBlocks),
        entryLabel: entryLabel
    }
}

function addFileToLocations(bytecode, file) {
    if (typeof file !== "string") throw new Error("Location without file information");

    for (const block of bytecode) {
        recursorAddFileLocation(block.code, file);
        recursorAddFileLocation(block.jumps, file);
    }
}

function recursorAddFileLocation(bytecodeArray, file) {
    for (const bytecode of bytecodeArray) {
        const loc = bytecode.location;
        if (loc.fileStack === undefined) loc.fileStack = [loc.file];

        const lastItem = loc.fileStack[loc.fileStack.length - 1];
        if (lastItem !== file) loc.fileStack.push(file);

        recursorAddFileLocation(bytecode.args, file);
    }
}

function constructDelegatorContinuationBranch(dependencyBlocks, constantPool, ast, entryBlock, dependencyShaPrefix) {
    const branchBlock = {
        label: constantPool.subblockLabel("static-link-continuation"),
        code: [],
        jumps: []
    };
    const continuationVariable = dependencyShaPrefix + "-" + systemVariableNames.COROUTINE_CONTINUATION_PREFIX;

    for (const block of dependencyBlocks) {
        if (block.continuationIndex > 0) {
            branchBlock.jumps.push(emitBytecodeWithLocation(bytecodeSpec.jmp_l_cond, [
                emitBytecodeWithLocation(bytecodeSpec.cmp_eq, [
                    emitBytecodeWithLocation(bytecodeSpec.getvar, [
                        emitConstantWithLocation(continuationVariable, constantPool, ast)
                    ], ast),
                    emitConstantWithLocation(block.continuationIndex, constantPool, ast)
                ], ast),
                emitConstantWithLocation(block.label, constantPool, ast)
            ], ast));
        }
    }
    branchBlock.jumps.push(emitBytecodeWithLocation(bytecodeSpec.jmp_l, [
        emitConstantWithLocation(entryBlock, constantPool, ast)
    ], ast));

    return branchBlock;
}

function sha(t) {
    return createHash("sha1").update(t).digest("hex");
}

async function functionCallToBytecode(ast, constantPool) {
    var toBeCalled = await valueToBytecodeBlocks(ast.func, constantPool);
    var args = await argumentListToBytecodeAsPositionalThenNamed(ast.args, constantPool);

    return {
        computation: emitBytecodeWithLocation(bytecodeSpec.callfunction,
            [toBeCalled.computation].concat(args.boundedCalculations)
            , ast),
        dependentBlocks: [].concat(toBeCalled.dependentBlocks, args.dependentBlocks)
    }

}

async function tableLiteralToBytecode(ast, constantPool) {
    var elems = await argumentListToBytecodeAsProperties(ast.elems, constantPool);

    return {
        computation: emitBytecodeWithLocation(bytecodeSpec.construct_table, elems.boundedCalculations, ast),
        dependentBlocks: elems.dependentBlocks
    };
}

async function tailedValueToBytecode(ast, constantPool) {
    var head = await valueToBytecodeBlocks(ast.head, constantPool);
    var tail = await valueToBytecodeBlocks(ast.tail, constantPool);

    return {
        computation: emitBytecodeWithLocation(bytecodeSpec.getprop, [
            head.computation,
            tail.computation
        ], ast),
        dependentBlocks: [].concat(head.dependentBlocks, tail.dependentBlocks)
    };
}

async function argumentListToBytecodeAsPositionalThenNamed(ast, constantPool) {
    var blocks = [];
    var calculationsPositional = [], calculationsNamed = [];

    for (var i = 0; i < ast.args.length; i++) {
        var arg = ast.args[i];
        var argBc = await valueToBytecodeBlocks(arg, constantPool);

        if (arg.type == "TitledArgument") {
            //argBc should be a construct_relation bytecode. Use its args!
            if (argBc.computation.code != bytecodeSpec.construct_relation.code) throw new Error("Unexpected non-construct_relation");

            calculationsNamed.push(argBc.computation.args[0], argBc.computation.args[1]);
        } else {
            calculationsPositional.push(argBc.computation);
        }

        blocks = blocks.concat(argBc.dependentBlocks);
    }

    return {
        dependentBlocks: blocks,
        boundedCalculations: [].concat(
            calculationsPositional,
            [emitConstantWithLocation(calculationsPositional.length, constantPool, ast)],

            calculationsNamed,
            [emitConstantWithLocation(calculationsNamed.length / 2, constantPool, ast)]
        )
    }
}

async function argumentListToBytecodeAsFuncParamNames(ast, constantPool) {
    var blocks = [];
    var calculations = [];

    for (var i = 0; i < ast.args.length; i++) {
        var arg = ast.args[i];
        var argBc = await valueToBytecodeBlocks(arg, constantPool);

        if (arg.type == "TitledArgument") {
            //argBc should be a construct_relation bytecode. Use its args!
            if (argBc.computation.code != bytecodeSpec.construct_relation.code) throw new Error("Unexpected non-construct_relation");

            calculations.push(argBc.computation.args[0], argBc.computation.args[1]);
        } else {
            //make a default value of `undefined`
            calculations.push(argBc.computation, emitConstantWithLocation(undefined, constantPool, ast));
        }

        blocks = blocks.concat(argBc.dependentBlocks);
    }

    return {
        dependentBlocks: blocks,
        boundedCalculations: calculations.concat(emitConstantWithLocation(ast.args.length, constantPool, ast))
    }
}

async function argumentListToBytecodeAsProperties(ast, constantPool) {
    var blocks = [];
    var calculations = [];

    for (var i = 0; i < ast.args.length; i++) {
        var arg = ast.args[i];
        var argBc = await valueToBytecodeBlocks(arg, constantPool);

        if (arg.type == "TitledArgument") {
            //argBc should be a construct_relation bytecode. Use its args!
            if (argBc.computation.code != bytecodeSpec.construct_relation.code) throw new Error("Unexpected non-construct_relation");

            calculations.push(argBc.computation.args[0], argBc.computation.args[1]);
        } else {
            //make an index
            calculations.push(emitConstantWithLocation(i, constantPool, ast), argBc.computation);
        }

        blocks = blocks.concat(argBc.dependentBlocks);
    }

    return {
        dependentBlocks: blocks,
        boundedCalculations: calculations.concat(emitConstantWithLocation(ast.args.length, constantPool, ast))
    }
}

/**
 * 
 * @param {Ast} valueAst 
 * @returns {Bytecode}
 */
function primitiveValueToSimpleBytecode(valueAst, constantPool) {
    switch (valueAst.type) {
        case "Identifier":
            return emitConstantWithLocation(valueAst.value, constantPool, valueAst);
        case "VariableReference":
            return variableReferenceToSimpleBytecode(valueAst, constantPool);
        case "StringLiteral":
            return emitConstantWithLocation(valueAst.str, constantPool, valueAst);
        case "NumericValue":
            return emitConstantWithLocation(valueAst.v, constantPool, valueAst);
        case "BooleanLiteral":
            return emitConstantWithLocation(valueAst.value, constantPool, valueAst);
        case "UnitValue":
            return emitConstantWithLocation(unitwrap(valueAst), constantPool, valueAst);
    };
    return undefined
}

function variableReferenceToSimpleBytecode(ast, constantPool) {
    let varname = ast.variable.value;

    if (constantPool.isGlobalName(varname) == false) varname = constantPool.universalPrefix + "-" + varname;

    return emitBytecodeWithLocation(bytecodeSpec.getvar, [
        emitConstantWithLocation(varname, constantPool, ast)
    ], ast);
}

function unitwrap(ast) {
    if (ast.value.type != "NumericValue") throw "Value isn't a number";
    if (ast.unit.type != "Identifier") throw "Unit isn't an identifier";
    return [ast.value.v, ast.unit.value];
}


/**
 * 
 * @param {string} comp 
 * @returns {Bytecode}
 */
function getOperationBytecodeInstruction(comp) {
    switch (comp) {
        case "<": return bytecodeSpec.cmp_lt;
        case "<=": return bytecodeSpec.cmp_lte;
        case ">": return bytecodeSpec.cmp_gt;
        case ">=": return bytecodeSpec.cmp_gte;
        case "==": return bytecodeSpec.cmp_eq;
        case "!=": return bytecodeSpec.cmp_neq;

        case "+": return bytecodeSpec.add;
        case "-": return bytecodeSpec.subtr;
        case "*": return bytecodeSpec.mul;
        case "/": return bytecodeSpec.div;
        case "%": return bytecodeSpec.mod;
        case "^":
        case "**": return bytecodeSpec.exp;
    }
    throw "Unrecognized comparison " + comp;
}

async function provideStatementToBytecode(ast, label, constantPool, nextLabel) {
    var val = await valueToBytecodeBlocks(ast.value, constantPool);

    const continuationVariable = constantPool.universalPrefix + "-" + systemVariableNames.COROUTINE_CONTINUATION_PREFIX + "@0";
    const continuationIndex = constantPool.getCoroutineContinuation();

    const jumpBlockLabel = constantPool.subblockLabel("provide-continuation");

    return [{
        label: label,
        code: [
            emitBytecodeWithLocation(bytecodeSpec.spec_setvar, [
                emitConstantWithLocation(continuationVariable, constantPool, ast),
                emitConstantWithLocation(continuationIndex, constantPool, ast)
            ], ast),
            emitBytecodeWithLocation(bytecodeSpec.ret, [
                val.computation
            ], ast)
        ],
        jumps: []
    },
    {
        label: jumpBlockLabel,
        continuationIndex: continuationIndex,
        code: [],
        jumps: [jumpToLabel(nextLabel, constantPool)]
    },
    {
        label: PROGRAM_INIT_PREFIX + continuationVariable,
        code: [
            emitBytecodeWithLocation(bytecodeSpec.spec_setvar, [
                emitConstantWithLocation(continuationVariable, constantPool, ast),
                emitConstantWithLocation(-1, constantPool, ast)
            ], ast)
        ],
        jumps: []
    }].concat(val.dependentBlocks);
}

/**
 * 
 * @param {*} ast 
 * @param {*} constantPool 
 * @returns {Promise<valueComputationBytecode>}
 */
async function titledArgToBytecode(ast, constantPool) {
    var title = await valueToBytecodeBlocks(ast.name, constantPool);
    var value = await valueToBytecodeBlocks(ast.value, constantPool);

    return {
        dependentBlocks: [].concat(title.dependentBlocks, value.dependentBlocks),
        computation: emitBytecodeWithLocation(bytecodeSpec.construct_relation, [
            title.computation,
            value.computation
        ], ast)
    };
}

function passStatementToBytecode(ast, label, constantPool, nextLabel) {
    return [{
        label: label,
        code: [emitBytecodeWithLocation(bytecodeSpec.pass, [], ast)],
        jumps: [jumpToLabel(nextLabel, constantPool)]
    }];
}

async function valueStatementToBytecode(ast, label, constantPool, nextLabel) {
    var val = await valueToBytecodeBlocks(ast.call, constantPool);

    return [{
        label: label,
        //pop the value once it's calculated to ensure a clean stack :)
        code: [emitBytecodeWithLocation(bytecodeSpec.pop, [val.computation], ast)],
        jumps: [jumpToLabel(nextLabel, constantPool)]
    }].concat(val.dependentBlocks);
}

async function gotoStatementToBytecode(ast, label, constantPool) {
    var pathName = await valueToBytecodeBlocks(ast.path, constantPool);

    return [{
        label: label,
        code: [],
        jumps:
            [emitBytecodeWithLocation(bytecodeSpec.jmp_l,
                [emitBytecodeWithLocation(bytecodeSpec.add, [
                    emitBytecodeWithLocation(bytecodeSpec.add, [
                        emitConstantWithLocation("s/" + constantPool.universalPrefix + "/", constantPool, ast),
                        pathName.computation], ast),
                    emitConstantWithLocation("/0", constantPool, ast)
                ], ast)
                ], ast)]
    }].concat(pathName.dependentBlocks)
}

async function returnStatementToBytecode(ast, label, constantPool) {
    var val = await valueToBytecodeBlocks(ast.value, constantPool);

    return [
        {
            label: label,
            code: [
                emitBytecodeWithLocation(bytecodeSpec.ret, [val.computation], ast)
            ],
            jumps: [],
        }
    ].concat(val.dependentBlocks)
}

/**
 * 
 * @param {*} ast 
 * @param {*} label 
 * @param {*} constantPool 
 * @param {*} nextLabel 
 * @returns {Block[]}
 */
function skipStatementToBytecode(ast, label, constantPool, stateCountInPath) {

    if (ast.skip.type != "NumericValue") throw { text: "non-numeric skip! You may only use `skip <n>`, where n is a number.", location: ast.location };

    var targetLabelName = calculateSkipToLabel(label, ast.skip.v, stateCountInPath, ast.location);

    return [{
        label: label,
        code: [],
        jumps: [jumpToLabel(targetLabelName, constantPool)]
    }];
}

function nextStatementToBytecode(ast, label, constantPool, stateCountInPath) {
    var toLabel = calculateSkipToLabel(label, 1, stateCountInPath, ast.location);
    return [{
        label: label,
        code: [],
        jumps: [jumpToLabel(toLabel, constantPool)]
    }];
}

function calculateSkipToLabel(currentLabel, offsetNum, stateCountInPath, locationForError) {
    var stateBlockParams = currentLabel.split("/");
    var thisStateIndex = +stateBlockParams[3];

    if (isNaN(thisStateIndex)) throw {
        kind: "ERROR",
        text: "Sanity check failed; skip-to basis is non-numeric"
    };

    var prefix = "s/" + stateBlockParams[1] + "/" + stateBlockParams[2] + "/";

    var skipToIndex = thisStateIndex + offsetNum;

    if (isNaN(skipToIndex) || isNaN(stateCountInPath) || stateCountInPath == 0) {
        throw {
            text: "something went wrong converting a `next` or `skip` statement to bytecode. Make sure that you don't have one inside a function!",
            location: locationForError
        }
    }

    var skipToIndexNorm = skipToIndex % stateCountInPath;
    if (skipToIndexNorm < 0) skipToIndexNorm += stateCountInPath;

    return prefix + skipToIndexNorm;
}

async function letStatementToBytecode(ast, label, constantPool, nextLabel) {
    const variableName = ast.variable && ast.variable.value;
    if (typeof variableName !== "string") throw {
        kind: "ERROR",
        text: "Attempt to set a non-string variable",
        hints: ["Make sure that it doesn't start with a number!"],
        location: ast.location
    }
    const prefixedVariableName = constantPool.universalPrefix + "-" + variableName;

    const value = await valueToBytecodeBlocks(ast.value, constantPool);

    return [{
        label: label,
        code: [
            emitBytecodeWithLocation(bytecodeSpec.setvar, [
                emitConstantWithLocation(prefixedVariableName, constantPool, ast),
                value.computation
            ], ast)
        ],
        jumps: [jumpToLabel(nextLabel, constantPool)]
    }].concat(value.dependentBlocks);
}

/**
 * 
 * @returns {Block[]}
 */
async function letPropertyToBytecode(ast, label, constantPool, nextLabel) {
    if (ast.variable.type != "TailedValue") throw "Attempt to inappropriately `let` an unsettable value";

    var headBytecode = await valueToBytecodeBlocks(ast.variable.head, constantPool);
    var tailBytecode = await valueToBytecodeBlocks(ast.variable.tail, constantPool);
    var setValueBytecode = await valueToBytecodeBlocks(ast.value, constantPool);

    return [{
        label: label,
        code: [emitBytecodeWithLocation(bytecodeSpec.setprop, [
            headBytecode.computation,
            tailBytecode.computation,
            setValueBytecode.computation
        ], ast)],
        jumps: [jumpToLabel(nextLabel, constantPool)]

    }].concat(headBytecode.dependentBlocks, tailBytecode.dependentBlocks, setValueBytecode.dependentBlocks);

}

/**
 * 
 * @param {*} ast 
 * @param {*} constantPool 
 * @returns {Promise<valueComputationBytecode>}
 */
async function functionLiteralToBytecode(ast, constantPool) {
    var endBlockLabel = constantPool.subblockLabel("func-end");
    var endBlock = {
        label: endBlockLabel,
        code: [emitBytecodeWithLocation(bytecodeSpec.ret, [emitConstantWithLocation(undefined, constantPool, ast)], ast)],
        jumps: []
    };

    var functionBodyLabel = constantPool.subblockLabel("func-body");
    var functionBody = await statementToBytecodeBlocks(ast.body, functionBodyLabel, constantPool, endBlockLabel, NaN, NaN);

    var entryBlockLabel = constantPool.subblockLabel("func-enter");
    var entryBlock = {
        label: entryBlockLabel,
        code: [],
        jumps: [jumpToLabel(functionBodyLabel, constantPool)]
    };

    var argsCode = await argumentListToBytecodeAsFuncParamNames(ast.args, constantPool);

    var functionConstructionCode = emitBytecodeWithLocation(bytecodeSpec.makefunction_l,
        [emitConstantWithLocation(entryBlockLabel, constantPool, ast)].concat(argsCode.boundedCalculations)
        , ast);

    return {
        computation: functionConstructionCode,
        dependentBlocks: [entryBlock, endBlock].concat(argsCode.dependentBlocks, functionBody)
    };
}

async function functionDefToBytecode(ast, label, constantPool, nextLabel) {
    var functionLiteral = await functionLiteralToBytecode(ast, constantPool);

    const variableName = constantPool.universalPrefix + "-" + ast.name.value;

    return [{
        label: label,
        code: [emitBytecodeWithLocation(bytecodeSpec.setvar, [
            emitConstantWithLocation(variableName, constantPool, ast),
            functionLiteral.computation
        ], ast)],
        jumps: [jumpToLabel(nextLabel, constantPool)]
    }].concat(functionLiteral.dependentBlocks);
}

async function compoundStatementToBytecode(ast, label, constantPool, nextLabel, stateCountInPath) {

    var bcBlocks = [];

    var statements = ast.state.statement;
    var statementLabels = statements.map((x, i) => constantPool.subblockLabel(label, "blockStatement" + i));

    for (var i = 0; i < statements.length; i++) {
        var lbl = statementLabels[i];
        var nextStmtLbl = statementLabels[i + 1] || nextLabel;
        var nextStmt = await statementToBytecodeBlocks(statements[i], lbl, constantPool, nextStmtLbl, stateCountInPath);

        bcBlocks = bcBlocks.concat(nextStmt);
    }

    bcBlocks.push({
        label: label,
        code: [],
        jumps: [jumpToLabel(statementLabels[0], constantPool)]
    });

    return bcBlocks;
}

/**
 * 
 * @param {*} ast 
 * @param {*} constantPool 
 * @returns {Promise<valueComputationBytecode>}
 */
async function operationToBytecode(ast, constantPool) {
    var op = getOperationBytecodeInstruction(ast.operator);

    var left = await valueToBytecodeBlocks(ast.left, constantPool);
    var right = await valueToBytecodeBlocks(ast.right, constantPool);

    return {
        computation: emitBytecodeWithLocation(op, [
            left.computation,
            right.computation
        ], ast)
        ,
        dependentBlocks: [].concat(left.dependentBlocks, right.dependentBlocks)
    };
}

async function ifStatementToBytecode(ast, label, constantPool, nextLabel, stateCountInPath) {
    /**
     * IfStatements look like this in bytecode:
     * 
     * ifStatement:
     *     if(condition) jump ifStatementIfTrue
     *     jump ifStatmentIfFalse
     * ifStatementIfTrue:
     *     //...body...
     *     jump ifStatmentEnd
     * ifStatementIfFalse
     *     //...body...
     *     jump ifStatementEnd
     * ifStatementEnd
     *     jump NEXT_LABEL
     */

    var labels = {
        ifStatementIfTrue: constantPool.subblockLabel(label, "if-true"),
        ifStatementIfFalse: constantPool.subblockLabel(label, "if-false"),
        ifStatmentEnd: constantPool.subblockLabel(label, "if-end")
    };

    var ifTrueCode = await statementToBytecodeBlocks(ast.statement, labels.ifStatementIfTrue, constantPool, labels.ifStatmentEnd, stateCountInPath);
    var ifFalseCode = await statementToBytecodeBlocks(ast.elseClause, labels.ifStatementIfFalse, constantPool, labels.ifStatmentEnd, stateCountInPath);

    var ifStmtEndingBlock = {
        label: labels.ifStatmentEnd,
        code: [],
        jumps: [jumpToLabel(nextLabel, constantPool)]
    };

    var conditional = await valueToBytecodeBlocks(ast.conditional, constantPool);

    var ifStmtStartingBlock = {
        label: label,
        code: [],
        jumps: [
            emitBytecodeWithLocation(bytecodeSpec.jmp_l_cond, [
                conditional.computation,
                emitConstantWithLocation(labels.ifStatementIfTrue, constantPool, ast)
            ], ast),
            jumpToLabel(labels.ifStatementIfFalse, constantPool)
        ]
    };

    return [ifStmtEndingBlock, ifStmtStartingBlock].concat(ifTrueCode, ifFalseCode, conditional.dependentBlocks);
}

async function afterStatementToBytecode(ast, label, constantPool, nextLabel, stateCountInPath) {

    /*
        AfterStatements look like this in bytecode:
        
        {+state entry label}:
            setvar TMP_1 true
        afterStatement (main):
            if (getvar TMP_1) jump afterStatementInit
            jump afterStatementCheckingBody
        afterStatementInit:
            setvar TMP_1 false
            setvar TMP_2 getUnitValueCurrent(unitvalue)
            jump afterStatementCheckingBody
        afterStatementCheckingBody:
            if(abs_difference(getUnitValueCurrent(unitvalue), TMP_2) >= unitvalue) jump afterStatmentIfFinished
            jump afterStatementDone
        afterStatementIfFinished:
            //...body...
            jump afterStatementDone
        afterStatementDone:
            jump NEXT_LABEL
    */
    var tmp1name = emitConstantWithLocation(constantPool.tempVar(), constantPool, ast);
    var tmp2name = emitConstantWithLocation(constantPool.tempVar(), constantPool, ast);

    var const_false = emitConstantWithLocation(false, constantPool, ast);
    var const_true = emitConstantWithLocation(true, constantPool, ast);

    var unitvalue = await valueToBytecodeBlocks(ast.unitValue, constantPool);

    var labels = {
        afterStatementInit: constantPool.subblockLabel(label, "after-init"),
        afterStatementCheckingBody: constantPool.subblockLabel(label, "after-checking"),
        afterStatementIfFinished: constantPool.subblockLabel(label, "after-if-finished-body"),
        afterStatementDone: constantPool.subblockLabel(label, "after-done")
    };

    for (const labelname in labels) {
        labels[labelname] = emitConstantWithLocation(labels[labelname], constantPool, ast);
    }

    var stateEntryBlock = makeStateinitBlock(
        [
            emitBytecodeWithLocation(bytecodeSpec.setvar, [tmp1name, const_true], ast)
        ], constantPool
    );

    var afterstmtMain = {
        label: label,
        code: [],
        jumps: [
            emitBytecodeWithLocation(bytecodeSpec.jmp_l_cond, [
                emitBytecodeWithLocation(bytecodeSpec.getvar, [tmp1name], ast),
                labels.afterStatementInit
            ], ast),
            emitBytecodeWithLocation(bytecodeSpec.jmp_l, [labels.afterStatementCheckingBody], ast)
        ]
    };

    var afterstmtInit = {
        label: labels.afterStatementInit.__value,
        code: [
            emitBytecodeWithLocation(bytecodeSpec.setvar, [
                tmp1name, const_false
            ], ast),
            emitBytecodeWithLocation(bytecodeSpec.setvar, [
                tmp2name, emitBytecodeWithLocation(bytecodeSpec.unit_currentv, [unitvalue.computation], ast)
            ], ast)
        ],
        jumps: [emitBytecodeWithLocation(bytecodeSpec.jmp_l, [labels.afterStatementCheckingBody], ast)]
    };

    var afterstmtCheckingBody = {
        label: labels.afterStatementCheckingBody.__value,
        code: [],
        jumps: [
            emitBytecodeWithLocation(bytecodeSpec.jmp_l_cond, [
                emitBytecodeWithLocation(bytecodeSpec.cmp_gte, [
                    emitBytecodeWithLocation(bytecodeSpec.abs_dif, [
                        emitBytecodeWithLocation(bytecodeSpec.unit_currentv, [
                            unitvalue.computation
                        ], ast),
                        emitBytecodeWithLocation(bytecodeSpec.getvar, [
                            tmp2name
                        ], ast)
                    ], ast),
                    unitvalue.computation
                ], ast),
                labels.afterStatementIfFinished
            ], ast),
            emitBytecodeWithLocation(bytecodeSpec.jmp_l, [
                labels.afterStatementDone
            ], ast)
        ]
    };

    var afterstmtIfFinishedBody = await statementToBytecodeBlocks(ast.statement, labels.afterStatementIfFinished.__value, constantPool, labels.afterStatementDone.__value, stateCountInPath);

    var afterstmtDone = {
        label: labels.afterStatementDone.__value,
        code: [],
        jumps: [jumpToLabel(nextLabel, constantPool)]
    };

    return [].concat(
        unitvalue.dependentBlocks,
        afterstmtIfFinishedBody,
        [
            stateEntryBlock,
            afterstmtMain,
            afterstmtInit,
            afterstmtCheckingBody,
            afterstmtDone
        ]
    );
}

function makeStateinitBlock(bytecode, pool) {
    var label = STATE_INIT_PREFIX + " " + pool.tempVar();

    return {
        label: label,
        code: bytecode,
        jumps: []
    };
}

/**
 * @typedef {object} Bytecode
 * @property {number} code
 * @property {*?} __value
 * @property {Bytecode[]} args
 * @property {object} location
 */