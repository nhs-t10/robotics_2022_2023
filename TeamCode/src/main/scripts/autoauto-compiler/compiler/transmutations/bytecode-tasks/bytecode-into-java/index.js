const cpool = require("./constant-pool");
const makeJava = require("./make-java");
const makeHelperClass = require("./make-helper-class");

var path = require("path");

module.exports = function run(context) {
    var package = context.inputs["get-result-package"];

    var constantPool = cpool();

    var bytecode = context.inputs["bytecode-flatten"];
    var codes = [];

    //recalculate value codes
    //push the codes into an array. This will later be set-ified, which means that the `switch` statement can be dense
    //  (0,1,2,3), not (0,300,400)-- better optimized!
    bytecode.forEach(x => {
        if (x.hasOwnProperty("__value")) x.code = constantPool.getCodeFor(x.__value);
        codes.push(x.code);
    });
    var denseCodes = Array.from(new Set(codes));
    bytecode.forEach(x => {
        x.denseCode = denseCodes.indexOf(x.code);
    });

    var helperClassName = context.resultBaseFileName.replace(".java", "") + "____AutoautoProgram";
    var helperFile = path.join(context.resultDir, helperClassName + ".java");

    var helperClassContent = makeHelperClass(package, helperClassName, makeJava(denseCodes, constantPool, bytecode));

    context.writtenFiles[helperFile] = helperClassContent;

    context.output = `return new ${helperClassName}();`;
    context.status = "pass";
}