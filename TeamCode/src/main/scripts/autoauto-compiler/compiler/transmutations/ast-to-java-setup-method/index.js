var astTools = require("./ast-tools");

module.exports = function run(context) {
    context.output = astTools(context.inputs["text-to-syntax-tree"]);
    context.status = "pass";
}