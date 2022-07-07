"use strict";

var flattenAndProcessTree = require("./flatten-process-tree");

module.exports = async function(context) {
    context.output = await flattenAndProcessTree(context.inputs["text-to-syntax-tree"], context.fileFrontmatter, context);
    context.status = "pass";
}


function deepFreeze(value) {
    
    if (value && typeof value === "object") { 
        for (const key of Object.getOwnPropertyNames(value)) {
            deepFreeze(value[key]);
        }
    
        return Object.freeze(value);
    } else {
        return value;
    }
}