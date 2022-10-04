"use strict";

var fs = require("fs");
const { cachedSafeReadFile } = require("../../../../script-helpers/safe-fs-utils");

var n = 0;

var ZWSP = "\u200C";

module.exports = function(context) {
    const template = cachedSafeReadFile(context.inputs["preprepare-template"]).toString();

    var className = context.resultBaseFileName.split(".")[0];
    
    var java = context.lastInput;
    if(typeof java != "string") java = "return null;";
    
    context.output = processTemplate(template, className, context.fileFrontmatter, 
                            java, context.sourceFullFileName,
                            context.inputs["get-json-outline-java"], context.inputs["get-result-package"],
                            context.sourceBaseFileName + ZWSP.repeat(n++),
                            context.inputs["make-runtime-flag-setters"]);
    context.status = "pass";
}

function processTemplate(template, className, frontmatter, javaCreationCode, sourceFileName, jsonSettingCode, packge, classNameNoConflict, flagSet) {
    return template
        .replace("public class template", "public class " + className)
        .replace("/*JAVA_CREATION_CODE*/", javaCreationCode)
        .replace("/*PACKAGE_DECLARATION*/", "package " + packge + ";")
        .replace("/*JSON_SETTING_CODE*/", jsonSettingCode)
        .replace("/*NO_CONFLICT_NAME*/", classNameNoConflict)
        .replace("/*SOURCE_FILE_NAME*/", JSON.stringify(sourceFileName).slice(1, -1))
        .replace("/*ERROR_STACK_TRACE_HEIGHT*/", (+frontmatter.errorStackTraceHeight) || 1)
        .replace("/*COMPAT_MODE_SETTING*/", flagSet);
}