const bytecodeSpec = require("../bytecode-spec");

module.exports = {
    jumpToLabel: jumpToLabel,
    emitBytecodeWithLocation: emitBytecodeWithLocation,
    emitConstantWithLocation: emitConstantWithLocation
}


/**
 * 
 * @param {string} lbl 
 * @param {import("../constant-pool").constantpool} pool 
 * @returns {import("./ast-to-bytecode").Bytecode}
 */
function jumpToLabel(lbl, pool) {
    return emitBytecodeWithLocation(bytecodeSpec.jmp_l, [
        emitConstantWithLocation(lbl, pool, {})
    ], undefined, pool.currentFile);
}

/**
 * 
 * @param {*} cons 
 * @param {import("../constant-pool").constantpool} pool 
 * @param {import("../../text-to-syntax-tree/parser").AutoautoASTElement?} ast 
 * @returns {import("./ast-to-bytecode").Bytecode}
 */
function emitConstantWithLocation(cons, pool, ast) {
    return emitBytecodeWithLocation({ code: pool.getCodeFor(cons), __value: cons }, [], ast, pool.currentFile);
}

/**
 * 
 * @param {number|{code:number}} code 
 * @param {import("./ast-to-bytecode").Bytecode[]} bcArgs
 * @param {object} ast 
 * @param {string?} fileAddress
 * @returns {import("./ast-to-bytecode").Bytecode}
 */
function emitBytecodeWithLocation(code, bcArgs, ast, fileAddress) {

    var r = {};
    if (typeof code === "number") r.code = code;
    else Object.assign(r, code);

    r.args = arrShallowCp(bcArgs);
    if (ast && ast.location) r.location = ast.location;
    else r.location = makeSyntheticLocation(fileAddress);

    return r;
}

function makeSyntheticLocation(file) {

    if (typeof file !== "string") throw new Error("Attempt to construct a synthetic location without an associated file");

    return {
        file: file,
        synthetic: true
    };
}

function arrShallowCp(arr) {
    return arr.map(x => {
        var c = {};
        Object.assign(c, x);
        return c;
    });
}