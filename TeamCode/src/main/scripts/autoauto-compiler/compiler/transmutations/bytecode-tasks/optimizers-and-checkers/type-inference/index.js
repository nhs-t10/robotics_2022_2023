const bc = require("../../bc");
const bytecodeSpec = require("../../bytecode-spec");
const typeSystemCreator = require("./type-system-creator");

module.exports = function run(context) {
    var bytecode = context.inputs["single-static"];
    var javaFunctions = context.inputs["java-function-loader"];
    
    var typeSystem = typeSystemCreator(javaFunctions);

    Object.values(bytecode).forEach(x => processBytecodeBlock(x, bytecode, typeSystem));

    context.output = typeSystem.__t;
    context.status = "pass";
}

function processBytecodeBlock(block, blocks, typeSystem) {
    if (block.__hasTypes) return;
    addAllLocalVariablesToTypeSystem(block.code, blocks, typeSystem);
    addAllLocalVariablesToTypeSystem(block.jumps, blocks, typeSystem);
    block.__hasTypes = true;
}

function addAllLocalVariablesToTypeSystem(codeArray, blocks, typeSystem) {
    for (var i = 0; i < codeArray.length; i++) {
        addLocalTypes(codeArray[i], blocks, typeSystem);
    }
}
function addLocalTypes(bcInstruction, blocks, typeSystem) {
    addAllLocalVariablesToTypeSystem(bcInstruction.args, blocks, typeSystem);

    bcInstruction.__typekey = calcType(bcInstruction, typeSystem.makeAnonymousTypeName(), typeSystem, blocks);
}

function gatherReturnTypeFrom(entryBlock, blocks, typeSystem) {
    var possibleTypes = processBlockChildrenForReturnTypes(entryBlock, blocks, typeSystem);
    var uniqTypes = Array.from(new Set(possibleTypes));

    if (uniqTypes.length == 1) return uniqTypes[0];
    else return { type: "union", types: uniqTypes };

}
function findReturnTypeInSingleBlock(block) {
    for (var i = 0; i < block.code.length; i++) {
        if (block.code[i].code == bytecodeSpec.ret.code) {
            return block.code[i].args[0].__typekey;
        }
    }
    return "undefined";
}
function processBlockChildrenForReturnTypes(block, blocks, typeSystem) {
    processBytecodeBlock(block, blocks, typeSystem);

    var returnTypes = [findReturnTypeInSingleBlock(block)];

    var jmps = block.jumps;
    var children = jmps.map(x => x.args[x.args.length - 1].__value);

    for (var i = 0; i < children.length; i++) {
        var childBlock = blocks[children[i]];
        if (!childBlock) throw "bad key " + children[i];

        returnTypes = returnTypes.concat(
            processBlockChildrenForReturnTypes(childBlock, blocks, typeSystem)
        );
    }

    return returnTypes;
}

function calcType(instruction, currentTypeKey, typeSystem, blocks) {
    if (instruction.hasOwnProperty("__value")) return getAutoautoTypeOfPrimitive(instruction.__value);

    switch (instruction.code) {
        case bytecodeSpec.cmp_lt.code:
        case bytecodeSpec.cmp_lte.code:
        case bytecodeSpec.cmp_eq.code:
        case bytecodeSpec.cmp_neq.code:
        case bytecodeSpec.cmp_gte.code:
        case bytecodeSpec.cmp_gt.code:
            return "boolean";

        case bytecodeSpec.construct_table.code:
            return assembleObjectType(instruction, currentTypeKey, typeSystem);
        case bytecodeSpec.construct_relation.code:
            return assembleRelationType(instruction, currentTypeKey, typeSystem);
        case bytecodeSpec.dup.code:
        case bytecodeSpec.swap.code:
            throw new Error("Bad use of dup or swap!");

        //things that don't return anything
        case bytecodeSpec.pop.code:
        case bytecodeSpec.jmp_i.code:
        case bytecodeSpec.jmp_i_cond.code:
        case bytecodeSpec.jmp_l.code:
        case bytecodeSpec.jmp_l_cond.code:
        case bytecodeSpec.yieldto_l.code:
        case bytecodeSpec.yieldto_i.code:
        case bytecodeSpec.spec_setvar.code:
        case bytecodeSpec.ret.code:
        case bytecodeSpec.pass.code:
            return "undefined";
        case bytecodeSpec.setvar.code:
            return setVariableType(instruction, currentTypeKey, typeSystem);

        case bytecodeSpec.unit_currentv.code:
            return "number";
        case bytecodeSpec.abs_dif.code:
            return "number";
        case bytecodeSpec.getvar.code:
            return getVariableType(instruction, currentTypeKey, typeSystem);

        case bytecodeSpec.callfunction.code:
            return getFunctionReturnType(instruction, currentTypeKey, typeSystem);

        case bytecodeSpec.getprop.code:
            return getGetpropReturnType(instruction, currentTypeKey, typeSystem);

        case bytecodeSpec.setprop.code:
            recordSetpropType(instruction, currentTypeKey, typeSystem);
            return "undefined";

        case bytecodeSpec.add.code:
            return getBinaryOperatorType(instruction, currentTypeKey, typeSystem, "+");
        case bytecodeSpec.mul.code:
            return getBinaryOperatorType(instruction, currentTypeKey, typeSystem, "*");
        case bytecodeSpec.div.code:
            return getBinaryOperatorType(instruction, currentTypeKey, typeSystem, "/");
        case bytecodeSpec.exp.code:
            return getBinaryOperatorType(instruction, currentTypeKey, typeSystem, "**");
        case bytecodeSpec.mod.code:
            return getBinaryOperatorType(instruction, currentTypeKey, typeSystem, "%");
        case bytecodeSpec.subtr.code:
            return getBinaryOperatorType(instruction, currentTypeKey, typeSystem, "-");

        case bytecodeSpec.makefunction_l.code:
            return recordFunctionType(instruction, currentTypeKey, typeSystem, blocks);

        default:
            console.error(instruction);
            var f = bc[instruction.code];
            console.error("untyped bytecode! " + (f ? f.mnemom : instruction.code));
    }
}

function recordFunctionType(instruction, currentTypeKey, typeSystem, blocks) {
    var t = {
        type: "function",
        args: [],
        argnames: [],
        varargs: "undefined",
        return: "*"
    };

    var lbl = instruction.args[0].__value;
    var fEntryBlock = blocks[lbl];
    if (!fEntryBlock) throw "Malformed function! no " + lbl;
    t.return = gatherReturnTypeFrom(fEntryBlock, blocks, typeSystem);

    for (var i = 2; i < instruction.args.length; i += 2) {
        var argname = instruction.args[i - 1].__value;
        t.argnames.push(argname + "");
        t.args.push(makeArgumentType(instruction.args[i].__typekey, argname, lbl, typeSystem, instruction.location));
    }

    typeSystem.upsertType(currentTypeKey, t, instruction.location);

    return currentTypeKey;
}

function makeArgumentType(type, name, functionBodyLabel, typeSystem, loc) {
    var tName = "var " + functionBodyLabel + "|arg:" + name;
    typeSystem.upsertType(tName, {
        type: "alias",
        typeTo: type
    }, loc);
    return tName;
}

function getBinaryOperatorType(instruction, key, typeSystem, op) {
    typeSystem.upsertType(key, {
        type: "binary_operator",
        op: op,
        left: instruction.args[0].__typekey,
        right: instruction.args[1].__typekey
    }, instruction.location);
    return key;
}

function recordSetpropType(setpropInstr, key, typeSystem) {
    var prop = setpropInstr.args[1];
    
    if("__value" in prop) {
        typeSystem.upsertType(setpropInstr.args[0].__typekey, {
            type: "object",
            properties: {
                [prop.__value + ""]: setpropInstr.args[2].__typekey
            }
        }, setpropInstr.location);
    } else {
        typeSystem.upsertType(setpropInstr.args[0].__typekey, {
            type: "object",
            properties: {},
            some: setpropInstr.args[2].__typekey
        }, setpropInstr.location);
    }
}

function getGetpropReturnType(getpropInstr, key, typeSystem) {

    var propInstr = getpropInstr.args[1];
    var propText = null;
    if ("__value" in propInstr) {
        propText = propInstr.__value + "";
    }
    
    typeSystem.upsertType(key, {
        type: "object_apply",
        object: getpropInstr.args[0].__typekey,
        key: propText
    }, getpropInstr.location);

    return key;
}

function getFunctionReturnType(callfunctionInstr, key, typeSystem) {
    var functionTypeName = callfunctionInstr.args[0].__typekey;

    var namedArgs = {};
    var posArgs = [];

    var namedArgCount = callfunctionInstr.args[callfunctionInstr.args.length - 1].__value;
    var i = callfunctionInstr.args.length - 2;
    for (; namedArgCount > 0; i -= 2, namedArgCount--) {
        namedArgs[callfunctionInstr.args[i - 1].__value] = callfunctionInstr.args[i].__typekey;
    }
    var posArgCount = callfunctionInstr.args[i].__value;
    i--;
    for (; posArgCount > 0; i--, posArgCount--) {
        posArgs.unshift(callfunctionInstr.args[i].__typekey);
    }


    typeSystem.upsertType(key, {
        type: "apply",
        operand: functionTypeName,
        positionalArguments: posArgs,
        namedArguments: namedArgs
    }, callfunctionInstr.location);

    return key;
}

function getVariableType(getvarInstruction, key, typeSystem) {
    var vname = getvarInstruction.args[0].__value;
    if (!vname.__phi) return "var " + vname;

    typeSystem.upsertType(key, {
        type: "union",
        types: vname.__phi.map(x => "var " + x)
    }, getvarInstruction.location);
    return key;

}
function setVariableType(setvarInstruction, key, typeSystem) {

    var vname = setvarInstruction.args[0].__value;

    var vtype = setvarInstruction.args[1].__typekey;

    typeSystem.upsertType("var " + vname, { type: "alias", typeTo: vtype }, setvarInstruction.location);

    return "undefined";
}

function getAutoautoTypeOfPrimitive(v) {
    if (v && typeof v[0] == "number" && typeof v[1] == "string") return "unit";
    else return typeof v;
}

function assembleRelationType(crInstr, currentTypeKey, typeSystem) {
    var t = {
        type: "object",
        some: "undefined",
        properties: {}
    };

    var k = crInstr.args[0].__value;
    var vT = crInstr.args[1].__typekey;
    t.properties[k] = vT;

    typeSystem.upsertType(currentTypeKey, t, crInstr.location);
    return currentTypeKey;
}

function assembleObjectType(constructTableInstr, currentTypeKey, typeSystem) {
    var t = {
        type: "object",
        some: { type: "union", types: [] },
        properties: {}
    };
    for (var i = 1; i < constructTableInstr.args.length; i += 2) {
        var key = constructTableInstr.args[i - 1].__value;
        if (key === undefined) continue;

        var valueType = constructTableInstr.args[i].__typekey;
        t.properties[key] = valueType;
    }

    typeSystem.upsertType(currentTypeKey, t, constructTableInstr.location);

    return currentTypeKey;
}

/**
 * @typedef {Bytecode} typedBytecode
 * @property __typekey
 */