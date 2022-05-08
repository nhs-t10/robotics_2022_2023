const emptyblock = require("./check-sources/emptyBlock");
const noStatepaths = require("./check-sources/noStatepaths");
const singleStatementBlock = require("./check-sources/singleStatementBlock");
const unknownStatepath = require("./check-sources/unknownStatepath");
const unreachableStatepath = require("./check-sources/unreachableStatepath");
const uselessBlock = require("./check-sources/uselessBlock");

module.exports = function() {
    return [
        emptyblock,
        noStatepaths,
        singleStatementBlock,
        unknownStatepath,
        unreachableStatepath,
        uselessBlock
    ]
};