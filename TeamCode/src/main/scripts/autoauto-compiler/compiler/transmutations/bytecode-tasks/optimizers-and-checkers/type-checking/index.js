const bytecodeSpec = require("../../bytecode-spec");
const verifyTypeSystem = require("./verify-type-system");

/**
 * 
 * @param {import("../../../index").TransmutateContext} context 
 */
module.exports = function run(context) {
    var typeSystem = context.inputs["type-inference"];
    var bytecode = context.inputs["single-static"];

    verifyTypeSystem(typeSystem, context.sourceFullFileName);

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
    bcArr.forEach(x => rmTypeCruftBc(x))
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
    return cruftedVarname
        .replace(/@\d+$/, "")
        .replace(/.+:([^:]+)/, "$1");
}

function isVariableManipulationCode(instr) {
    return instr.code == bytecodeSpec.setvar.code || instr.code == bytecodeSpec.getvar.code;
}