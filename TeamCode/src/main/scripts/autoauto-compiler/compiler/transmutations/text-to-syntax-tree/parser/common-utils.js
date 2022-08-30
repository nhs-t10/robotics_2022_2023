"use strict";



module.exports = {
    expect: expect,
    improperContextError: improperContextError,
    unexpectedError: unexpectedError
}

/**
 * 
 * @param {import("./token-stream").TokenStream} tokenStream 
 * @param {import("./base-token-types").tokenId} name 
 * @param {string} message 
 * @param {string[]?} hints 
 * @returns {import("./lexer").AutoautoToken} the token which was expected
 */
function expect(tokenStream, name, message, hints) {
    const p = tokenStream.pop();
    
    
    if (!p || p.name != name) throw {
        location: p.location,
        message: message + " " + p.name + " '" + p.content + "'",
        hints: hints
    };

    return p;
}


/**
 * 
 * @param {string} message 
 * @param {import(".").Location} location 
 * @param {string[]?} hints
 */
function improperContextError(message, location, hints) {
    return {
        message: message,
        location: location,
        type: "IMPROPER_CONTEXT",
        hints: hints,
        ctx: new Error().stack
    }
}

/**
 * 
 * @param {string} name 
 * @param {import(".").Location} location 
 * @param {import("./base-token-types").tokenId} tokenName 
 * @param {string[]?} hints 
 */
function unexpectedError(name, location, tokenName, hints) {
    return {
        hints: hints,
        message: "Unexpected " + name,
        type: "UNEXPECTED",
        code: tokenName,
        location: location,
        ctx: new Error().stack
    }
}