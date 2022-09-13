"use strict";

const { collectComments, unexpectedError, improperContextError } = require("./common-utils");
const parseStatement = require("./statement-parsing");

// http://journal.stuffwithstuff.com/2011/03/19/pratt-parsers-expression-parsing-made-easy/

/**
 * @param {import("./token-stream").TokenStream} tokenStream 
 * @param {string} file
 */
module.exports = function(tokenStream, file) {
    return parseFile(tokenStream, file);
}


/**
 * @param {import("./token-stream").TokenStream} tokenStream
 * @param {string} file
 * @returns {import(".").AutoautoASTElement}
 */
function parseFile(tokenStream, file) {
    
    const locStart = tokenStream.peek().location.start;
    
    if (tokenStream.peek().name == "DOLLAR_SIGN") parseFrontmatter(tokenStream);
    
    /** @type {AutoautoStatepathElement} */
    let statepaths = [];
    
    
    if (tokenStream.peek().name != "HASHTAG") statepaths.push(parseUnlabeledStatepath(tokenStream, file));
    
    while (tokenStream.peek().name != "EOF") statepaths.push(parseLabeledStatepath(tokenStream, file));
    
    const locEnd = tokenStream.pop().location.end;
    
    return {
        type: "Program",
        location: { start: locStart, end: locEnd, file: file},
        statepaths: statepaths
    }
}

/**
 * 
 * @param {import("./token-stream").TokenStream} tokenStream 
 */
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

/**
 * @typedef {object} AutoautoStatepathElement
 * @property {string} label
 * @property {"LabeledStatepath"} type
 * @property {import(".").Location} location
 * @property {AutoautoStatepathContent} statepath
 */

/**
 * 
 * @param {import("./token-stream").TokenStream} tokenStream 
 * @param {string} file 
 * @returns {AutoautoStatepathElement}
 */
function parseUnlabeledStatepath(tokenStream, file) {
    const spContent = parseStatepathContent(tokenStream, file);
    return {
        label: "<init>",
        type: "LabeledStatepath",
        location: spContent.location,
        statepath: spContent
    }
}

/**
 * 
 * @param {import("./token-stream").TokenStream} tokenStream 
 * @param {string} file 
 * @returns {AutoautoStatepathElement}
 */
function parseLabeledStatepath(tokenStream, file) {
    const hashtag = tokenStream.pop();
    if(hashtag.name != "HASHTAG") {
        if (hashtag.name == "DOLLAR_SIGN") {
            throw improperContextError("There must be only one frontmatter, before all statepaths", hashtag.location, [
                "Combine all frontmatters: make sure that there is only 1 set of dollar signs, and all frontmatter is inside them"
            ]);
        }
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
        statepath: parseStatepathContent(tokenStream, file)
    }
}

/**
 * @typedef {object} AutoautoStatepathContent
 * @property {"Statepath"} type
 * @property {import(".").Location} location
 * @property {AutoautoState[]} states
 */

/**
 * 
 * @param {import("./token-stream").TokenStream} tokenStream 
 * @param {string} file 
 * @returns {AutoautoStatepathContent}
 */
function parseStatepathContent(tokenStream, file) {
    let states = [];
    while(tokenStream.peek().name != "HASHTAG" &&
        tokenStream.peek().name != "EOF") {
        states.push(parseState(tokenStream, file));
    }
    
    if(states.length == 0) {
        throw improperContextError("There must be at least one state in each statepath", tokenStream.peek().location, [
            "If you want a placeholder that doesn't do anything, try adding a `pass` statement"
        ])
    }
    
    return {
        type: "Statepath",
        location: { start: states[0].location.start, end: states[states.length - 1].location.end, file: file },
        states: states
    }
}

/**
 * @typedef {object} AutoautoState
 * @property {"State"} type
 * @property {import(".").Location} location
 * @property {AutoautoStatement[]} statement
 */

/**
 * 
 * @param {import("./token-stream").TokenStream} tokenStream 
 * @param {string} file 
 * @returns {AutoautoState}
 */
function parseState(tokenStream, file) {
    let statements = [];
    
    while(tokenStream.peek().name != "SEMICOLON" && 
        tokenStream.peek().name != "HASHTAG" &&
        tokenStream.peek().name != "EOF") {
            statements.push(parseStatement(tokenStream, file));
            
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
        location: { start: statements[0].location.start, end: statements[statements.length - 1].location.end, file: file },
        statement: statements
    }
}

