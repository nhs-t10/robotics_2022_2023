var flattenAndProcessTree = require("./flatten-process-tree");

module.exports = async function(context) {
    context.output = await flattenAndProcessTree(context.inputs["text-to-syntax-tree"], context.fileFrontmatter, context);
    context.status = "pass";
}