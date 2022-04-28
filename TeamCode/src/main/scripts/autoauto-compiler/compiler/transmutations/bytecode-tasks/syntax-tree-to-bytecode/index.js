var flattenAndProcessTree = require("./flatten-process-tree");

module.exports = function(context) {
    context.output = flattenAndProcessTree(context.inputs["text-to-syntax-tree"])
    context.status = "pass";
}