const prattParse = require("./pratt-parse");
const tokenstream = require("./token-stream");


/**
 * @typedef {object} AutoautoASTElement
 * @property {SourceCodeLocation} location
 * @property {string} type
 * @implements {Object.<string, *>}
 */

/**
 * @typedef {RealSourceCodeLocation|SyntheticSourceCodeLocation} SourceCodeLocation
 */

/**
 * @typedef {object} SyntheticSourceCodeLocation
 * @property {string} file
 * @property {true} synthetic
 * @property {string[]?} filestack
 */

/**
 * @typedef {object} RealSourceCodeLocation
 * @property {Cursor} start
 * @property {Cursor} end
 * @property {string} file
 * @property {string[]?} filestack
 */

/**
 * @typedef {object} Cursor
 * @property {number} line
 * @property {number} column
 * @property {number} offset
 */

/**
 * 
 * @param {string} text 
 * @param {string} fileAddress
 * @returns 
 */
module.exports = function (text, fileAddress) {
    var stream = tokenstream(text, fileAddress);
    return prattParse(stream, fileAddress);
}