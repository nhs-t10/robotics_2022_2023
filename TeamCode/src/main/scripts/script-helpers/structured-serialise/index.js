"use strict";

const toBuffer = require("./object-to-buffer");
const fromBuffer = require("./buffer-to-object");

module.exports = {
    magic: require("./magic"),
    toBuffer: toBuffer,
    fromBuffer: fromBuffer,
    structuredClone: clone
}

/**
 * @template {*} T
 * @param {T} o 
 * @returns {T}
 */
function clone(o) {
    if(typeof global.structuredClone === "function") return global.structuredClone(o);
    
    return fromBuffer(toBuffer(o));
}