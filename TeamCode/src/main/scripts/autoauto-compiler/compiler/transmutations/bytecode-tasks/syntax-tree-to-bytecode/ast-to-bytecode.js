const systemVariableNames = require("../../../data/system-variable-names");
var bytecodeSpec = require("../bytecode-spec");

var STATE_INIT_PREFIX = "{+STATE_INIT ";

module.exports = treeBlockToBytecode;

//this gets a "tree block", which is equivalent to a state.
function treeBlockToBytecode(block, constantPool) {
    
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
        ], {})]
    }
    
    var blocks = [];
    
    for(var i = 0; i < block.treeStatements.length; i++) {
        var thisLabel = statementLabels[i];
        var nextLabel = statementLabels[i + 1] || stateEndBlock.label;
        
        var stmtBlocks = statementToBytecodeBlocks(block.treeStatements[i], thisLabel, constantPool, nextLabel, block.stateCountInPath);
        
        blocks = blocks.concat(stmtBlocks);
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
    var stateInitBlocks = allStatementBlocks.filter(x=>x.label.startsWith(STATE_INIT_PREFIX));
    
    if(stateInitBlocks.length == 0) return;
    
    //make the last state init block go to wherever the start block was going
    stateInitBlocks[stateInitBlocks.length - 1].jumps = startBlock.jumps;
    //make the start block go to the first state init block
    startBlock.jumps = [jumpToLabel(stateInitBlocks[0].label, constantPool)];
    
    for(var i = 0; i < stateInitBlocks.length - 1; i++) {
        stateInitBlocks[i].jumps = [
            jumpToLabel(stateInitBlocks[i + 1].label, constantPool)
        ];
    }
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
 * @param {ConstantPool} constantPool 
 * @param {string} afterThisJumpToLabel 
 * @returns {Block[]}
 */
function statementToBytecodeBlocks(ast, label, constantPool, afterThisJumpToLabel, stateCountInPath) {
    
    switch (ast.type) {
        case "SkipStatement":
            return skipStatementToBytecode(ast, label, constantPool, stateCountInPath);
        case "NextStatement":
            return nextStatementToBytecode(ast, label, constantPool, stateCountInPath);
        case "LetPropertyStatement":
            return letPropertyToBytecode(ast, label, constantPool, afterThisJumpToLabel);
        case "FunctionDefStatement":
            return functionDefToBytecode(ast, label, constantPool, afterThisJumpToLabel);    
        case "Block":
            return compoundStatementToBytecode(ast, label, constantPool, afterThisJumpToLabel, stateCountInPath);
        case "PassStatement":
            return passStatementToBytecode(ast, label, constantPool, afterThisJumpToLabel);
        case "IfStatement":
            return ifStatementToBytecode(ast, label, constantPool, afterThisJumpToLabel, stateCountInPath);
        case "LetStatement":
            return letStatementToBytecode(ast, label, constantPool, afterThisJumpToLabel);
        case "GotoStatement":
            return gotoStatementToBytecode(ast, label, constantPool);
        case "ReturnStatement":
            return returnStatementToBytecode(ast, label, constantPool);
        case "NextStatement":
            return nextStatementToBytecode(ast, label, constantPool, stateCountInPath);
        case "AfterStatement":
            return afterStatementToBytecode(ast, label, constantPool, afterThisJumpToLabel, stateCountInPath);
        case "ValueStatement":
            return valueStatementToBytecode(ast, label, constantPool, afterThisJumpToLabel);
        case "ProvideStatement":
            return provideStatementToBytecode(ast, label, constantPool, afterThisJumpToLabel);
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
 * @returns {valueComputationBytecode}
 */
function valueToBytecodeBlocks(valueAst, constantPool) {
    var simpleBytecode = primitiveValueToSimpleBytecode(valueAst, constantPool);
    
    //if it was able to make that into a simple bytecode array, great!
    if(simpleBytecode !== undefined) {
        return {
            dependentBlocks: [],
            computation: simpleBytecode
        };
    }

    //otherwise, try the more-complex ones.
    switch(valueAst.type) {
        case "FunctionLiteral": 
            return functionLiteralToBytecode(valueAst, constantPool)
        case "OperatorExpression":
        case "ComparisonOperator":
            return operationToBytecode(valueAst, constantPool);
        case "TitledArgument":
            return titledArgToBytecode(valueAst, constantPool);
        case "ArgumentList":
            throw "Must call argumentListToBytecode directly, as it returns an array of bytecodes!";
        case "DynamicValue":
            return valueToBytecodeBlocks(valueAst.value, constantPool);
        case "ArrayLiteral":
            return tableLiteralToBytecode(valueAst, constantPool);
        case "FunctionCall":
            return functionCallToBytecode(valueAst, constantPool);
        case "TailedValue":
            return tailedValueToBytecode(valueAst, constantPool);
        case "DelegatorExpression":
            return delegatorExpressionToBytecode(valueAst, constantPool);
    }
    throw new Error("Unknown value type " + valueAst.type);
}

function delegatorExpressionToBytecode(ast, constantPool) {
    var simulatedCall = {
        type: "FunctionCall",
        location: ast.location,
        func: {
            type: "VariableReference",
            location: ast.location,
            variable: {
                type: "Identifier",
                location: ast.location,
                value: "delegate"
            }
        },
        args: {
            type: "ArgumentList",
            location: ast.location,
            len: ast.args.len + 1,
            args: [ast.delegateTo].concat(ast.args.args)
        }
    };

    return functionCallToBytecode(simulatedCall, constantPool);
}

function functionCallToBytecode(ast, constantPool) {
    var toBeCalled = valueToBytecodeBlocks(ast.func, constantPool);
    var arguments = argumentListToBytecodeAsPositionalThenNamed(ast.args, constantPool);
    
    return {
        computation: emitBytecodeWithLocation(bytecodeSpec.callfunction, 
            [toBeCalled.computation].concat(arguments.boundedCalculations)
        , ast),
        dependentBlocks: [].concat(toBeCalled.dependentBlocks, arguments.dependentBlocks)
    }
    
}

function tableLiteralToBytecode(ast, constantPool) {
    var elems = argumentListToBytecodeAsProperties(ast.elems, constantPool);
    
    return {
        computation: emitBytecodeWithLocation(bytecodeSpec.construct_table, elems.boundedCalculations, ast),
        dependentBlocks: elems.dependentBlocks
    };
}

function tailedValueToBytecode(ast, constantPool) {
    var head = valueToBytecodeBlocks(ast.head, constantPool);
    var tail = valueToBytecodeBlocks(ast.tail, constantPool);
    
    return {
        computation: emitBytecodeWithLocation(bytecodeSpec.getprop, [
            head.computation,
            tail.computation
        ], ast),
        dependentBlocks: [].concat(head.dependentBlocks, tail.dependentBlocks)
    };
}

function argumentListToBytecodeAsPositionalThenNamed(ast, constantPool) {
    var blocks = [];
    var calculationsPositional = [], calculationsNamed = [];
    
    for (var i = 0; i < ast.args.length; i++) {
        var arg = ast.args[i];
        var argBc = valueToBytecodeBlocks(arg, constantPool);

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

function argumentListToBytecodeAsFuncParamNames(ast, constantPool) {
    var blocks = [];
    var calculations = [];

    for (var i = 0; i < ast.args.length; i++) {
        var arg = ast.args[i];
        var argBc = valueToBytecodeBlocks(arg, constantPool);

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

function argumentListToBytecodeAsProperties(ast, constantPool) {
    var blocks = [];
    var calculations = [];

    for (var i = 0; i < ast.args.length; i++) {
        var arg = ast.args[i];
        var argBc = valueToBytecodeBlocks(arg, constantPool);
        
        if (arg.type == "TitledArgument") {
            //argBc should be a construct_relation bytecode. Use its args!
            if(argBc.computation.code != bytecodeSpec.construct_relation.code) throw new Error("Unexpected non-construct_relation");
            
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
    switch(valueAst.type) {
        case "Identifier":
            return emitConstantWithLocation(valueAst.value, constantPool, valueAst);
        case "VariableReference":
            return emitBytecodeWithLocation(bytecodeSpec.getvar, [
                primitiveValueToSimpleBytecode(valueAst.variable, constantPool)
            ], valueAst);
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

function unitwrap(ast) {
    if(ast.value.type != "NumericValue") throw "Value isn't a number";
    if(ast.unit.type != "Identifier") throw "Unit isn't an identifier";
    return [ast.value.v, ast.unit.value];
}

/**
 * 
 * @param {*} lbl 
 * @param {*} pool 
 * @returns {Bytecode}
 */
function jumpToLabel(lbl, pool) {
    return emitBytecodeWithLocation(bytecodeSpec.jmp_l, [
        emitConstantWithLocation(lbl, pool, {})
    ], {});
}

/**
 * 
 * @param {string} comp 
 * @returns {Bytecode}
 */
function getOperationBytecodeInstruction(comp) {
    switch(comp) {
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

function provideStatementToBytecode(ast, label, constantPool, nextLabel) {
    var val = valueToBytecodeBlocks(ast.value, constantPool);

    return [{
        label: label,
        code: [emitBytecodeWithLocation(bytecodeSpec.spec_setvar, [
                emitConstantWithLocation(systemVariableNames.EXPORTS, constantPool, ast),
                val.computation
            ], ast)
        ],
        jumps: [jumpToLabel(nextLabel, constantPool)]
    }].concat(val.dependentBlocks);
}

/**
 * 
 * @param {*} ast 
 * @param {*} constantPool 
 * @returns {valueComputationBytecode}
 */
function titledArgToBytecode(ast, constantPool) {
    var title = valueToBytecodeBlocks(ast.name, constantPool);
    var value = valueToBytecodeBlocks(ast.value, constantPool);
    
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

function valueStatementToBytecode(ast, label, constantPool, nextLabel) {
    var val = valueToBytecodeBlocks(ast.call, constantPool);

    return [{
        label: label,
        //pop the value once it's calculated to ensure a clean stack :)
        code: [emitBytecodeWithLocation(bytecodeSpec.pop, [val.computation], ast)],
        jumps: [jumpToLabel(nextLabel, constantPool)]
    }].concat(val.dependentBlocks);
}

function gotoStatementToBytecode(ast, label, constantPool) {
    var pathName = valueToBytecodeBlocks(ast.path, constantPool);

    return [{
        label: label,
        code: [],
        jumps: 
            [emitBytecodeWithLocation(bytecodeSpec.jmp_l, 
                [emitBytecodeWithLocation(bytecodeSpec.add, [
                    emitBytecodeWithLocation(bytecodeSpec.add, [
                            emitConstantWithLocation("s/", constantPool, ast),
                            pathName.computation], ast),
                    emitConstantWithLocation("/0", constantPool, ast)
                    ], ast)
                ], ast)]
    }].concat(pathName.dependentBlocks)
}

function returnStatementToBytecode(ast, label, constantPool) {
    var val = valueToBytecodeBlocks(ast.value, constantPool);
    
    return [
        {
            label: label,
            code: [
                emitBytecodeWithLocation(bytecodeSpec.ret, [val.computation], ast)
            ],
            jumps:[],
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
    
    if(ast.skip.type != "NumericValue") throw { text: "non-numeric skip! You may only use `skip <n>`, where n is a number.", location: ast.location };
    
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
    var thisStateIndex = +stateBlockParams[2];

    var prefix = "s/" + stateBlockParams[1] + "/";
    
    var skipToIndex = thisStateIndex + offsetNum;

    if(isNaN(skipToIndex) || isNaN(stateCountInPath) || stateCountInPath == 0) {
        throw {
            text: "something went wrong converting a `next` or `skip` statement to bytecode. Make sure that you don't have one inside a function!",
            location: locationForError
        }
    }

    var skipToIndexNorm = skipToIndex % stateCountInPath;
    if(skipToIndexNorm < 0) skipToIndexNorm += stateCountInPath;
    
    return prefix + skipToIndexNorm; 
}

function letStatementToBytecode(ast, label, constantPool, nextLabel) {
    var variableName = valueToBytecodeBlocks(ast.variable, constantPool);
    var value = valueToBytecodeBlocks(ast.value, constantPool);

    return [{
        label: label,
        code: [
            emitBytecodeWithLocation(bytecodeSpec.setvar, [
                variableName.computation,
                value.computation
            ],ast)
        ],
        jumps: [jumpToLabel(nextLabel, constantPool)]
    }].concat(variableName.dependentBlocks, value.dependentBlocks);
}

/**
 * 
 * @returns {Block[]}
 */
function letPropertyToBytecode(ast, label, constantPool, nextLabel) {
    if(ast.variable.type != "TailedValue") throw "Attempt to inappropriately `let` an unsettable value";

    var headBytecode = valueToBytecodeBlocks(ast.variable.head, constantPool);
    var tailBytecode = valueToBytecodeBlocks(ast.variable.tail, constantPool);
    var setValueBytecode = valueToBytecodeBlocks(ast.value, constantPool);

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
 * @returns {valueComputationBytecode}
 */
function functionLiteralToBytecode(ast, constantPool) {
    var endBlockLabel = constantPool.subblockLabel("func_end");
    var endBlock = {
        label: endBlockLabel,
        code: [emitBytecodeWithLocation(bytecodeSpec.ret, [emitConstantWithLocation(undefined, constantPool, ast)], ast)],
        jumps: []
    };

    var functionBodyLabel = constantPool.subblockLabel("func_body");
    var functionBody = statementToBytecodeBlocks(ast.body, functionBodyLabel, constantPool, endBlockLabel, NaN, NaN);
    
    var entryBlockLabel = constantPool.subblockLabel("func_enter");
    var entryBlock = {
        label: entryBlockLabel,
        code: [],
        jumps: [jumpToLabel(functionBodyLabel, constantPool)]
    };

    var argsCode = argumentListToBytecodeAsFuncParamNames(ast.args, constantPool);

    var functionConstructionCode = emitBytecodeWithLocation(bytecodeSpec.makefunction_l, 
        [emitConstantWithLocation(entryBlockLabel, constantPool, ast)].concat(argsCode.boundedCalculations)
    , ast);
    
    return {
        computation: functionConstructionCode,
        dependentBlocks: [entryBlock, endBlock].concat(argsCode.dependentBlocks, functionBody)
    };
}

function functionDefToBytecode(ast, label, constantPool, nextLabel) {
    var functionLiteral = functionLiteralToBytecode(ast, constantPool);
    
    return [{
        label: label,
        code: [emitBytecodeWithLocation(bytecodeSpec.setvar, [
                primitiveValueToSimpleBytecode(ast.name, constantPool),
                functionLiteral.computation
            ], ast)],
        jumps: [jumpToLabel(nextLabel, constantPool)]
    }].concat(functionLiteral.dependentBlocks);
}

function compoundStatementToBytecode(ast, label, constantPool, nextLabel, stateCountInPath) {
    
    var bcBlocks = [];

    var statements = ast.state.statement;
    var statementLabels = statements.map((x,i)=>constantPool.subblockLabel(label, "blockStatement" + i));
    
    for(var i = 0; i < statements.length; i++) {
        var lbl = statementLabels[i];
        var nextStmtLbl = statementLabels[i + 1] || nextLabel;

        bcBlocks = bcBlocks.concat(statementToBytecodeBlocks(statements[i], lbl, constantPool, nextStmtLbl, stateCountInPath));
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
 * @returns {valueComputationBytecode}
 */
function operationToBytecode(ast, constantPool) {
    var op = getOperationBytecodeInstruction(ast.operator);

    var left = valueToBytecodeBlocks(ast.left, constantPool);
    var right = valueToBytecodeBlocks(ast.right, constantPool);
    
    return {
        computation: emitBytecodeWithLocation(op, [
                left.computation,
                right.computation
            ], ast)
        ,
        dependentBlocks: [].concat(left.dependentBlocks, right.dependentBlocks)
    };
}

function ifStatementToBytecode(ast, label, constantPool, nextLabel, stateCountInPath) {
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
        ifStatementIfTrue: constantPool.subblockLabel(label, "if_true"),
        ifStatementIfFalse: constantPool.subblockLabel(label, "if_false"),
        ifStatmentEnd: constantPool.subblockLabel(label, "if_end")
    };
    
    var ifTrueCode = statementToBytecodeBlocks(ast.statement, labels.ifStatementIfTrue, constantPool, labels.ifStatmentEnd, stateCountInPath);
    var ifFalseCode = statementToBytecodeBlocks(ast.elseClause, labels.ifStatementIfFalse, constantPool, labels.ifStatmentEnd, stateCountInPath);
    
    var ifStmtEndingBlock = {
        label: labels.ifStatmentEnd,
        code: [],
        jumps: [jumpToLabel(nextLabel, constantPool)]
    };

    var conditional = valueToBytecodeBlocks(ast.conditional, constantPool);

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

function afterStatementToBytecode(ast, label, constantPool, nextLabel, stateCountInPath) {

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

    var unitvalue = valueToBytecodeBlocks(ast.unitValue, constantPool);
    
    var labels = {
        afterStatementInit: constantPool.subblockLabel(label, "after_init"),
        afterStatementCheckingBody: constantPool.subblockLabel(label, "after_checking"),
        afterStatementIfFinished: constantPool.subblockLabel(label, "after_if_finished_body"),
        afterStatementDone: constantPool.subblockLabel(label, "after_done")
    };

    for(labelname in labels) {
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
            ],ast)
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
    
    var afterstmtIfFinishedBody = statementToBytecodeBlocks(ast.statement, labels.afterStatementIfFinished.__value, constantPool, labels.afterStatementDone.__value, stateCountInPath);

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
 * 
 * @param {*} cons 
 * @param {*} pool 
 * @param {*} ast 
 * @returns {object}
 */
function emitConstantWithLocation(cons, pool, ast) {
    return emitBytecodeWithLocation({ code: pool.getCodeFor(cons), __value: cons}, [], ast);
}

/**
 * @typedef {object} Bytecode
 * @property {number} code
 * @property {*?} __value
 * @property {Bytecode[]} args
 * @property {object} location
 */

/**
 * 
 * @param {number|{code:number}} code 
 * @param {Bytecode[]} bcArgs
 * @param {*} ast 
 * @returns {Bytecode}
 */
function emitBytecodeWithLocation(code, bcArgs, ast) {
    if(arguments.length != 3) throw "aaaa".fefds.tvgr;
    
    var r = {};
    if (typeof code === "number") r.code = code;
    else Object.assign(r, code);
    
    r.args = arrShallowCp(bcArgs);
    r.location = ast.location;
    
    return r;
}

function arrShallowCp(arr) {
    return arr.map(x=>{
        var c = {};
        Object.assign(c, x);
        return c;
    });
}