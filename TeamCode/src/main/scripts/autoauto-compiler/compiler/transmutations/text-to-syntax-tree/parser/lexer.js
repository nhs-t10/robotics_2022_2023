"use strict";

const baseTokenTypes = require("./base-token-types");

/**
 * 
 * @param {string} fileText 
 * @param {string} file 
 * @returns {Generator<AutoautoToken, never, unknown>}
 */
module.exports = function* lexer(fileText, file) {
    let line = 1, column = 0;
    
    for(var i = 0; i < fileText.length; i++) {
        const chr = fileText[i];
        column++;
        //skip whitespace
        if(chr == " " || chr == "\t" || chr == "\n" || chr == "\r") {
            //except for line accounting
            if(chr == "\n") {
                line++;
                column = 1;
            }
            continue;
        };
        const forRegex = fileText.substring(i);
        let matched = false;
        for(const lexRule of baseTokenTypes) {
            const regexResult = lexRule.regex.exec(forRegex);
            if(regexResult && regexResult.index == 0) {
                const lineColInMatch = lineColumnCount(regexResult[0], column, line);
                
                yield assembleResultToken(lexRule, regexResult, line, column, i, lineColInMatch.line, lineColInMatch.col, i + regexResult[0].length, file);
                
                column = lineColInMatch.col | 0;
                line = lineColInMatch.line | 0;
                
                i += regexResult[0].length - 1;
                matched = true;
                break;
            }
        }
        if(!matched) {
            const ln = { line: line, column: column, offset: i }
            throw {
                location: {start: ln, end: ln, file: file},
                message: "Invalid token '" + forRegex.substring(0, 10) + "...'"
            }
        }
    }
    
    while(true) {
        yield assembleResultToken({ name: "EOF" }, [""], line, column, fileText.length, line, column, fileText.length, file);
    }
}

/**
 * Calculates the amount of lines and columns in a piece of text, and then
 * returns a relative offset from the startCol and startLine, respectively.
 * @param {string} text 
 * @param {number} startCol 
 * @param {number} startLine 
 * @returns {{line: number, col: number}}
 */
function lineColumnCount(text, startCol, startLine) {
    let lines = occurances("\n", text);
    if(lines == 0) {
        return {
            line: startLine,
            col: startCol + text.length
        };
    } else {
        const lastLineCols = text.length - text.lastIndexOf("\n");
        return {
            line: startLine + lines,
            col: lastLineCols
        };
    }
}

/**
 * 
 * @param {string} char 
 * @param {string} str 
 * @returns {number}
 */
function occurances(char, str) {
    let count = 0;
    for(const chIndex of str) {
        if (chIndex == char) count++;
    }
    return count;
}

/**
 * 
 * @param {import("./base-token-types").LexerRule} lexRule 
 * @param {RegExpExecArray} regexResult 
 * @param {number} startLine 
 * @param {number} startColumn 
 * @param {number} startOffset 
 * @param {number} endLine 
 * @param {number} endColumn 
 * @param {number} endOffset 
 * @param {string} file 
 * @returns {AutoautoToken}
 */
function assembleResultToken(lexRule, regexResult, startLine, startColumn, startOffset, endLine, endColumn, endOffset, file) {
    return {
        name: lexRule.name,
        content: regexResult[0],
        regex: regexResult,
        location: {
            start: { line: startLine, column: startColumn, offset: startOffset },
            end: { line: endLine, column: endColumn, offset: endOffset },
            file: file
        }
    }
}

/**
 * @typedef {object} AutoautoToken
 * @property {import("./base-token-types").tokenId} name
 * @property {string} content
 * @property {RegExpExecArray} regex
 * @property {import(".").RealSourceCodeLocation} location
 */