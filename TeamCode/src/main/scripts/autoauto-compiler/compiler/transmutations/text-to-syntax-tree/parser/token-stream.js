"use strict";

const lexer = require("./lexer")

/**
 * @typedef {object} TokenStream 
 * @property {() => import("./lexer").AutoautoToken} peek
 * @property {() => import("./lexer").AutoautoToken} pop
 */

/**
 * 
 * @param {string} text 
 * @param {string} file 
 * @returns {AutoautoTokenStream}
 */
module.exports = function tokenStream(text, file) {
    const lexGenerator = lexer(text, file);
    
    let lookahead = undefined;
    
    function dumpComments() {
        while(peek().name == "COMMENT") {
            pop();
        }
    }
    function peek() {
        if (lookahead) return lookahead;
        else return lookahead = lexGenerator.next().value;
    }
    function pop() {
        if (lookahead != undefined) {
            let swap = lookahead;
            lookahead = undefined;
            return swap;
        } else {
            return nextToken;
        }
    }
    
    return {
        peek: function() {
            dumpComments();
            return peek();
        },
        pop: function() {
            dumpComments();
            return pop();
        }
    }
}