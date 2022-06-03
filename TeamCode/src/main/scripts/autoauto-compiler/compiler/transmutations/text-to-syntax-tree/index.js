var parser = require("./parser");
var makeHints = require("./hints");

module.exports = function(context) {
    try {
        context.output = parser(context.lastInput);
        context.status = "pass";
    } catch(e) {
        var hints = makeHints(context.lastInput, e);
        
        throw {
            kind: "ERROR",
            text: e.text || e.message || "Parsing error",
            original: e.message,
            hints: hints,
            location: e.location,
            fail: true
        }
    }
}