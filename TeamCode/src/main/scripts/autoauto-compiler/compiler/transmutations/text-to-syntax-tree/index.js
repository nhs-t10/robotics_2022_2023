"use strict";

var parser = require("./parser");
var makeHints = require("./hints");

module.exports = function(context) {
    try {
        context.output = parser(context.lastInput, context.sourceFullFileName);
        context.status = "pass";
    } catch(e) {
        var hints = makeHints(context.lastInput, e);
        
        throw {
            kind: "ERROR",
            text: "Parsing error",
            original: e.message,
            hints: hints,
            location: e.location,
            fail: true
        }
    }
}