

module.exports = {
    expect: expect,
    improperContextError: improperContextError,
    unexpectedError: unexpectedError
}

function expect(tokenStream, name, message, hints) {
    const p = tokenStream.pop();
    
    
    if (!p || p.name != name) throw {
        location: p.location,
        message: message + " " + p.name + " '" + p.content + "'",
        hints: hints
    };

    return p;
}



function improperContextError(message, location, hints) {
    return {
        message: message,
        location: location,
        type: "IMPROPER_CONTEXT",
        hints: hints,
        ctx: new Error().stack
    }
}

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