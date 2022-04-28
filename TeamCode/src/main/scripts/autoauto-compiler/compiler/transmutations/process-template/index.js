var fs = require("fs");
var template = fs.readFileSync(require("./template-filename")).toString();

var n = 0;

var ZWSP = "\u200C";

module.exports = function(context) {

    var className = context.resultBaseFileName.split(".")[0];
    
    var java = context.lastInput;
    if(typeof java != "string") java = "return null;";
    
    context.output = processTemplate(className, context.fileFrontmatter, 
                            java, context.sourceFullFileName,
                            context.inputs["get-json-outline-java"], context.inputs["get-result-package"],
                            context.sourceBaseFileName + ZWSP.repeat(n++),
                            context.inputs["make-runtime-flag-setters"]);
    context.status = "pass";
}

function processTemplate(className, frontMatter, javaCreationCode, sourceFileName, jsonSettingCode, package, classNameNoConflict, flagSet) {
    return template
        .replace("public class template", "public class " + className)
        .replace("/*JAVA_CREATION_CODE*/", javaCreationCode)
        .replace("/*PACKAGE_DECLARATION*/", "package " + package + ";")
        .replace("/*JSON_SETTING_CODE*/", jsonSettingCode)
        .replace("/*NO_CONFLICT_NAME*/", classNameNoConflict)
        .replace("/*SOURCE_FILE_NAME*/", JSON.stringify(sourceFileName).slice(1, -1))
        .replace("/*ERROR_STACK_TRACE_HEIGHT*/", (+frontMatter.errorStackTraceHeight) || 1)
        .replace("/*COMPAT_MODE_SETTING*/", flagSet);
}