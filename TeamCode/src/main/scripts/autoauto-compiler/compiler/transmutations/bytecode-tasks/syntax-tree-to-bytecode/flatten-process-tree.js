"use strict";

var cPool = require("../constant-pool");

var treeBlockToBytecodeBlock = require("./ast-to-bytecode");
const bytecodeSpec = require("../bytecode-spec");
const { PROGRAM_INIT_PREFIX } = require("./prefixes");

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
    
    let firstLabel = treeBlocks[0].label;
    
    if(blockRecords["ENTRY"]) {
        throw "entry block defined!"
    } else {
        blockRecords["ENTRY"] = {
            label: "ENTRY",
            code: [],
            jumps: [{
                code: bytecodeSpec.jmp_l.code,
                location: ast.location,
                args: [{ 
                    code: constantPool.getCodeFor(firstLabel), 
                    __value: firstLabel,
                    args: [],
                    location: ast.location
                }]
            }]
        };
    }
    
    if(context.isLibrary == false) {
        findAndRewriteProgramInitBlocks(blockRecords["ENTRY"], blockRecords);
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

/**
 * 
 * @param {import("./ast-to-bytecode").Block} startBlock 
 * @param {Object<string, import("./ast-to-bytecode").Block>} blockRecords
 * @returns 
 */
function findAndRewriteProgramInitBlocks(startBlock, blockRecords) {
    const allBlocks = Object.values(blockRecords);
    const initBlocks = allBlocks.filter(x => x.label.startsWith(PROGRAM_INIT_PREFIX));

    if (initBlocks.length == 0) return;
    

    for (var i = 0; i < initBlocks.length; i++) {        
        startBlock.code = initBlocks[i].code.concat(startBlock.code);
    }
    
    
}