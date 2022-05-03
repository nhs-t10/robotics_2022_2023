var checks = require("./checks/run-checks");

module.exports = function(context) {
    var suc = checks(context.inputs["text-to-syntax-tree"], context.sourceFullFileName, context.fileFrontmatter);
    
    if(suc) context.status = "pass";
}