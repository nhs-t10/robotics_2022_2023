var makeJava = require("./make-java");
var functionLookup = require("./function-lookup");

module.exports = function run(context) {    
    context.output = makeJava(context.inputs["text-to-syntax-tree"],
                            functionLookup(context.inputs["java-function-loader"]));
    context.status = "pass";
}