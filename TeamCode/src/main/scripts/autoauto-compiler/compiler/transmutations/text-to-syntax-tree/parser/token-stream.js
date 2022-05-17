const lexer = require("./lexer")

module.exports = function tokenStream(text) {
    const lexGenerator = lexer(text);
    
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
        if (lookahead) {
            let swap = lookahead;
            lookahead = undefined;
            return swap;
        } else {
            return lexGenerator.next().value;
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

/**
 * @typedef {object} TokenStream
 * @property {peekFunction} peek
 * @property {popFunction} pop
 */

/**
 * @callback peekFunction
 * @returns {import("./lexer").token}
 */


/**
 * @callback popFunction
 * @returns {import("./lexer").token}
 */
