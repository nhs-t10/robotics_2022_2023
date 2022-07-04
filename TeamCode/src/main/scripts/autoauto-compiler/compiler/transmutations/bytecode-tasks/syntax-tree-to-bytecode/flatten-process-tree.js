"use strict";

var cPool = require("../constant-pool");

var treeBlockToBytecodeBlock = require("./ast-to-bytecode");
const bytecodeSpec = require("../bytecode-spec");

module.exports = async function (ast, frontmatter, context) {
    var constantPool = cPool(context);
    var treeBlocks = programToTreeBlocks(ast, constantPool.universalPrefix);
    
    var bytecodeBlocks = [];
    
    for(const blk of treeBlocks) {
        const bcb = await treeBlockToBytecodeBlock(blk, constantPool, frontmatter);
        bytecodeBlocks.push(bcb);
    }
    
    var flattedBlocks = bytecodeBlocks.flat(1);
    
    var blockRecords = Object.fromEntries(flattedBlocks.map(x=>[x.label, x]));
    
    if(blockRecords["ENTRY"]) {
        throw "entry block defined!"
    } else {
        blockRecords["ENTRY"] = {
            label: "ENTRY",
            code: [],
            jumps: [{
                code: bytecodeSpec.jmp_l.code,
                location: undefined,
                args: [{ code: constantPool.getCodeFor(treeBlocks[0].label), __value: treeBlocks[0].label, args: [] }]
            }]
        };
    }
    
    return {
        blocks: blockRecords
    };
}

/**
 * @typedef {object} TreeBlock
 * @property {string} label
 * @property {AutoautoASTElement[]} treeStatements
 * @property {number} stateCountInPath
 */

/**
 * 
 * @param {AutoautoASTElement} program
 * @param {string} universalPrefix 
 * @returns {TreeBlock[]}
 */
function programToTreeBlocks(program, universalPrefix) {
    var blocks = program.statepaths.map(x=>
        x.statepath.states.map((y,i,a)=>({
            label: "s/" + universalPrefix + "/" + x.label + "/" + i,
            treeStatements: y.statement,
            stateCountInPath: a.length
        }))
    ).flat();
    return blocks;   
}