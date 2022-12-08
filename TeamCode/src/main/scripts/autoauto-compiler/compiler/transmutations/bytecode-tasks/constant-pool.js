"use strict";

const { createHash } = require("crypto");
const { sha } = require("../../../../script-helpers/sha-string");
const typeSystemCreator = require("./optimizers-and-checkers/type-inference/type-system-creator");

/**
 * @typedef {object} constantpool
 * @property {(v:*)=>number} getCodeFor
 * @property {string} currentFile
 * @property {string} universalPrefix
 * @property {()=>number} getCoroutineContinuation
 * @property {(name:string)=>boolean} isGlobalName
 * @property {()=>denseCodeMap} denseCodeMap
 * @property {(label:string, subcategory:string?)=>string} subblockLabel
 * @property {()=>string} tempVar
 * @property {Object.<string, string>} dependencyLabels a map of labels for each dependency, indexed by their absolute filename.
 */

/**
 * @typedef {object} denseCodeMap
 * @property {Object.<number, number>} map a map of indexes into the valueArray, keyed by their code.
 * @property {*[]} valueArray an array of all values in the pool
 * 
 */

/**
 * 
 * @param {import("..").TransmutateContext} fileContext 
 * @returns {constantpool} 
 */
module.exports = function (fileContext) {
    const fileAddress = fileContext.sourceFullFileName;
    const typeForGlobals = typeSystemCreator(fileContext.inputs["java-function-loader"]).__t;
    
    var pool = new Map();
    var invPool = {};
    var subId = 0, tempvars = 0, coroutineContinuations = 0, dependencyLabels = {};
    
    var fileAddressSha = sha(fileAddress);
    
    return {
        dependencyLabels: dependencyLabels,
        currentFile: fileAddress,
        universalPrefix: fileAddressSha,
        getCodeFor: function (cons) {

            //if it's an integer between 0 and 0xFFFFFF, use `loadint`.
            if (typeof cons === "number" && cons >= 0
                && (cons | 0) == cons && cons <= 0xFFFFFF) {
                return 0x0E000000 | cons;
            }

            if(pool.has(cons)) {
                return pool.get(cons);
            } else {
                var pid = pool.size;
                var code = 0x0F000000 | pid;
                
                pool.set(cons, code);
                invPool[code] = cons;
                
                return code;
            }
        },
        getCoroutineContinuation: function() {
            coroutineContinuations++;
            return coroutineContinuations;
        },
        isGlobalName: function(n) {
            return ("var " + n + "@0") in typeForGlobals;
        },
        denseCodeMap: function() {
            var map = {};
            var entries = Array.from(pool.entries());
            entries.forEach((x,i)=> {
                map[x[1]] = i;
            });
            
            var v = entries.map(x => x[0]);
            
            return {
                map: map,
                valueArray: v
            };
        },
        subblockLabel: function (label, subcategory) {
            if (arguments.length == 1) {
                subcategory = label;
                label = "subcat";
            }
            subId++
            return `${label}/${fileAddressSha}/${subcategory}/${subId.toString(16)}`;
        },
        tempVar: function () {
            return "@" + fileAddressSha + "-temp" + (tempvars++);
        }
    }
}