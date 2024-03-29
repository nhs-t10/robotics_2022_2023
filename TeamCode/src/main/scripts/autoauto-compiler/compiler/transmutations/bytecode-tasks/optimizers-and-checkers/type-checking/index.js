"use strict";

const { writeFileSync } = require("fs");
const androidStudioLogging = require("../../../../../../script-helpers/android-studio-logging");
const bytecodeSpec = require("../../bytecode-spec");
const { formatBc } = require("../../bytecode-tools");
const verifyTypeSystem = require("./verify-type-system");

/**
 * 
 * @param {import("../../../index").TransmutateContext} context 
 */
module.exports = async function run(context) {
    var typeSystem = context.inputs["type-inference"];
    var bytecode = context.inputs["single-static"];

    await verifyTypeSystem(typeSystem, context.sourceFullFileName);

    removeTypeCruft(bytecode);

    context.output = bytecode;
    context.status = "pass";
}

function removeTypeCruft(bytecode) {
    Object.values(bytecode).forEach(x => rmTypeCruftBlock(x));
}

function rmTypeCruftBlock(block) {
    rmTypeCruftBcarr(block.jumps);
    rmTypeCruftBcarr(block.code);
}
function rmTypeCruftBcarr(bcArr) {
    for(const b of bcArr) rmTypeCruftBc(b);
}
function rmTypeCruftBc(bc) {
    rmTypeCruftBcarr(bc.args);

    if (isVariableManipulationCode(bc)) {
        var vC = bc.args[0];
        vC.__value = replaceCruft(vC.__value);
    }
}

function replaceCruft(cruftedVarname) {
    if (cruftedVarname.__phi) cruftedVarname = cruftedVarname.__phi[0];
    
    //if it ends with an @0 but NOT an @@0, it's a built-in. Remove the file-scoping prefix.
    if(cruftedVarname.endsWith("@0") && !cruftedVarname.endsWith("@@0")) cruftedVarname = cruftedVarname.substring(cruftedVarname.lastIndexOf("-") + 1);
    
    return cruftedVarname
        .replace(/@\d+$/, "")
        .replace(/.+:([^:]+)/, "$1");
}

function isVariableManipulationCode(instr) {
    return instr.code == bytecodeSpec.setvar.code || instr.code == bytecodeSpec.getvar.code;
}