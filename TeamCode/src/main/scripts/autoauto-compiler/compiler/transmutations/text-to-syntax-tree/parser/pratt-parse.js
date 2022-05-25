const { collectComments, unexpectedError, improperContextError } = require("./common-utils");
const parseStatement = require("./statement-parsing");

// http://journal.stuffwithstuff.com/2011/03/19/pratt-parsers-expression-parsing-made-easy/

/**
 * @param {import("./token-stream").TokenStream} tokenStream 
 */
module.exports = function(tokenStream) {
    return parseFile(tokenStream);
}

/**
 * @param {import("./token-stream").TokenStream} tokenStream
 */
function parseFile(tokenStream) {
    
    const locStart = tokenStream.peek().location.start;
    
    if(tokenStream.peek().name == "DOLLAR_SIGN") parseFrontmatter(tokenStream);
    
    let statepaths = [];
    
    
    if(tokenStream.peek().name != "HASHTAG") statepaths.push(parseUnlabeledStatepath(tokenStream));
    
    while(tokenStream.peek().name != "EOF") statepaths.push(parseLabeledStatepath(tokenStream));
    
    const locEnd = tokenStream.pop().location.end;
    
    return {
        type: "Program",
        location: {start: locStart, end: locEnd},
        statepaths: statepaths
    }
}

function parseFrontmatter(tokenStream) {
    //Frontmatter is parsed by the compiler before the text-to-syntax-tree transmutation even *sees* the file.
    //therefore, it's a waste of time to parse it here.
    //instead, just skip over it!
    
    //pop the first dollar sign
    tokenStream.pop();
    
    //pop everything up to the next dollar sign
    while (tokenStream.peek().name != "DOLLAR_SIGN") {
        //If the coder leaves off the ending dollar sign, then this `while` loop will make it to the end of the file. That is not good.
        if (tokenStream.peek().name == "EOF") throw unexpectedError("End-of-File", tokenStream.peek().location, "EOF", ["Missing dollar sign to end frontmatter"]);
        
        tokenStream.pop();
    }
    
    //pop the last dollar sign.
    tokenStream.pop();
}

function parseUnlabeledStatepath(tokenStream) {
    const spContent = parseStatepathContent(tokenStream);
    return {
        label: "<init>",
        type: "LabeledStatepath",
        location: spContent.location,
        statepath: spContent
    }
}

function parseLabeledStatepath(tokenStream) {    
    const hashtag = tokenStream.pop();
    if(hashtag.name != "HASHTAG") {
        throw improperContextError("Expected a hashtag (#) to start a statepath label", hashtag.location);
    }
    
    const name = tokenStream.pop();
    const nameContent = name.content;
    
    const colon = tokenStream.pop();
    if (colon.name != "EQUALS_OR_COLON" || colon.content != ":") {
        throw improperContextError("Expected a colon (:) after the statepath's label", colon.location);
    }
    
    return {
        label: nameContent,
        type: "LabeledStatepath",
        location: name.location,
        statepath: parseStatepathContent(tokenStream)
    }
}

function parseStatepathContent(tokenStream) {
    let states = [];
    while(tokenStream.peek().name != "HASHTAG" &&
        tokenStream.peek().name != "EOF") {
        states.push(parseState(tokenStream));
    }
    
    if(states.length == 0) {
        throw improperContextError("There must be at least one state in each statepath", tokenStream.peek().location, [
            "If you want a placeholder that doesn't do anything, try adding a `pass` statement"
        ])
    }
    
    return {
        type: "Statepath",
        location: {start: states[0].location.start, end: states[states.length - 1].location.end},
        states: states
    }
}

function parseState(tokenStream) {    
    let statements = [];
    
    while(tokenStream.peek().name != "SEMICOLON" && 
        tokenStream.peek().name != "HASHTAG" &&
        tokenStream.peek().name != "EOF") {
            statements.push(parseStatement(tokenStream));
            
        if(tokenStream.peek().name == "COMMA") tokenStream.pop();
    }
    
    if(statements.length == 0) {
        throw improperContextError("There must be at least one statement in each state", tokenStream.peek().location, [
            "If you want a placeholder that doesn't do anything, try adding a `pass` statement"
        ]);
    }
    
    if(tokenStream.peek().name == "SEMICOLON") tokenStream.pop();
    
    return {
        type: "State",
        location: { start: statements[0].location.start, end: statements[statements.length - 1].location.end },
        statement: statements
    }
}

