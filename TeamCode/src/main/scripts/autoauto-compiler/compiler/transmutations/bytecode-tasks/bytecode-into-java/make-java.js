"use strict";

var bc = require("../bc");

var BYTECODE_PACKAGE = `dev.autoauto.model.bytecode`;
var PROGRAM_TYPE_PACKAGE = `dev.autoauto.model.programtypes`;

module.exports = function(denseCodes, constantPool, bytecode) {
    var denseConstantMap = constantPool.denseCodeMap();
    
    var bytecodesVarname = "bcs_" + Math.random().toString(16).substring(2);

    var instrs = bytecode.map(x=>`${bytecodesVarname}[${x.denseCode}]`).join(",");
    var bcMapArray = makeBytecodeMapArray(denseCodes, denseConstantMap.map);
    var values = denseConstantMap.valueArray.map(x=>wrapInPrimConstr(x));
    
    return {
        bytecodesVarname: bytecodesVarname,
        constants: `AutoautoValue[] constants = new AutoautoValue[] { ${values} };`,
        fullExtendsName: `${PROGRAM_TYPE_PACKAGE}.BytecodeEvaluationProgram`,
        instructions: `new ${BYTECODE_PACKAGE}.AutoautoBytecode[] {${instrs}}`,
        bytecodes: `${BYTECODE_PACKAGE}.AutoautoBytecode[] ${bytecodesVarname} = new ${BYTECODE_PACKAGE}.AutoautoBytecode[] {${bcMapArray}};`
    }
}

function wrapInPrimConstr(v) {
    var cons = {
        "string": "AutoautoString",
        "boolean": "AutoautoBooleanValue",
        "number": "AutoautoNumericValue",
        "undefined": "AutoautoUndefined",
        "object": "AutoautoUnitValue"
    }[typeof v];
    
    return `new ${cons}(${jLiteral(v)})`;
}

function jLiteral(v) {
    //must be an array, if it's an object
    if (typeof v === "object") return v.map(x => jLiteral(x)).join(",");

    if (typeof v === "number") return v + "d";
    else if (v === undefined) return "";
    else return JSON.stringify(v);
}


function makeBytecodeMapArray(codes, denseConstantMap) {
    return codes.map(x=> {
        if ((x & 0x0F000000) == 0x0F000000) {
            return `new ${BYTECODE_PACKAGE}.loadconst_Bytecode(constants[${denseConstantMap[x]}])`;
        } else if ((x & 0x0E000000) == 0x0E000000) {
            return `new ${BYTECODE_PACKAGE}.loadint_Bytecode(${x ^ 0x0E000000})`;
        } else {
            return `new ${BYTECODE_PACKAGE}.${bc[x].mnemom}_Bytecode()`
        }
    }).join(",");
}