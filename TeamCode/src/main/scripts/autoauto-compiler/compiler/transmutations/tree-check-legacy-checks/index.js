const checks = require("./checks/run-checks");
const getAllChecks = require("./checks/load-all")

module.exports = function(context) {
    var suc = checks(context.inputs["text-to-syntax-tree"], 
                context.sourceFullFileName, context.fileFrontmatter,
                getAllChecks());
    
    if(suc) context.status = "pass";
}