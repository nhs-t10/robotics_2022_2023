var path = require("path");
var fs = require("fs");

var directory = __dirname.split(path.sep);
var rootDirectory = directory.slice(0, directory.indexOf("TeamCode")).join(path.sep);
var robotFunctionsDirectory = path.join(rootDirectory, "TeamCode/gen/org/firstinspires/ftc/teamcode/auxilary/dsls/autoauto/runtime/robotfunctions");

var parser = require("../../../../../../script-helpers/javaparser/parser.js");



var processTemplate = require("./make-robotfunction-class.js");

var parserTools = require("../../../../../../script-helpers/parser-tools");
const safeFsUtils = require("../../../../../../script-helpers/safe-fs-utils.js");

module.exports = function(javaSource, preexistingNames) {
    try {
        var sourceWithoutComments = parserTools.stripComments(javaSource);
        var ast = parser.parse(sourceWithoutComments);
    } catch(e) {
        var classDeclarationIndex = javaSource.indexOf("public class");
        var classDeclarationIndexEnd = javaSource.indexOf("\n", classDeclarationIndex);
        console.error(e);
        console.error("Error parsing java source code! Make sure that it's correct: " + javaSource.substring(classDeclarationIndex,classDeclarationIndexEnd));

        return null;
    }

    var primaryType = ast.types.find(x=>x.modifiers.find(y=>y.keyword == "public"));
    if(!primaryType) throw "No public type in manager!";
    
    var primaryTypeName = primaryType.name.identifier;
    var packageName = packageNameToString(ast.package.name);
    var fullClassName = packageName + "." + primaryTypeName;
    
    
    var methods = findShimableMethods(primaryType);
    var overloads = groupMethodsIntoOverloads(methods);

    var processedMethodClassLocs = overloads.map(x=>generateRobotFunction(x, fullClassName, preexistingNames));
    return {
        sourceClass: fullClassName,
        methods: processedMethodClassLocs
    };
}

function packageNameToString(packageName) {
    if(packageName.node == "SimpleName") return packageName.identifier;
    else return packageNameToString(packageName.qualifier) + "." + packageName.name.identifier;
}

function generateRobotFunction(overload, definedClass, preexistingNames) {
    var name = overload.name;
    
    var noConflictName = name;
    while(preexistingNames.includes(noConflictName)) noConflictName += definedClass.split(".").slice(-1)[0];
    
    var typemaps = overload.overloadTyping;

    var functionIndexLines = [];
    
    var callMethodSource = "";
    
    var processedOverloads = 0;

    var argumentNames = [];
    
    for(var i = 0; ; i++) {
        var overloadsWithNArgs = typemaps.filter(x=>x.argTypes.length == i);
        //exit if we've done all of them.
        if(overloadsWithNArgs.length == 0) {
            if(processedOverloads >= typemaps.length) {
                break;
            } else {
                continue;
            }
        }
        processedOverloads += overloadsWithNArgs.length;

        callMethodSource += `if(args.length == ${i}) {`;

        var widestOverload = findWidestOverload(overloadsWithNArgs);
        functionIndexLines.push({ argCount: i, argumentTypes: widestOverload });

        if (widestOverload.argNames.length > argumentNames.length) argumentNames = widestOverload.argNames;

        
        if (widestOverload.returnType != "void") callMethodSource += `return new ${equivAutoautoClass(widestOverload.returnType)}(`;
        
        callMethodSource += `manager.${name}(`;
        callMethodSource += widestOverload.argTypes.map((x,k)=>`${caster(x)}args[${k}].${rawValueGetter(x)}`).join(",");

        if(widestOverload.returnType != "void") callMethodSource += `));`;
        else callMethodSource += "); return new AutoautoUndefined();";


        callMethodSource += `}`;
    }
    callMethodSource += `
    throw new org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.errors.AutoautoNoNativeMethodOverloadException("No ${noConflictName} with " + args.length + " args");
    `;

    noConflictName = replaceNumbers(noConflictName);

    var classname = noConflictName.charAt(0).toUpperCase() + noConflictName.substring(1) + "Function";
        
    var template = processTemplate(callMethodSource, definedClass, classname, argumentNames);

    var ourPath = path.join(robotFunctionsDirectory, classname + ".java");

    safeFsUtils.safeWriteFileEventually(ourPath, template);

    return {
        shimClassFunction: { nameToUseInAutoauto: noConflictName, javaImplementationClass: classname},
        functionTypes: functionIndexLines,
        originalSourceClass: definedClass
    }
}

function findWidestOverload(overloads) {
    var maxWidenessScore = 0;
    var maxScoreIndex = 0;

    for(var i = 0; i < overloads.length; i++) {
        var w = 0;
        overloads[i].argTypes.forEach(x=> w += getTypeWideness(x));

        if(w > maxWidenessScore) {
            maxWidenessScore = w;
            maxScoreIndex = i;
        }
    }

    return overloads[maxScoreIndex];
}

function getTypeWideness(type) {
    switch(type) {
        case "byte": return 1;
        case "short": return 1;
        case "int": return 1;
        case "long": return 2;
        case "float": return 3;
        case "double": return 3;
        case "boolean": return 1;
        case "char": return 4;
        case "String": return 5;

        default: return 0;
    }
}

function replaceNumbers(str) {
    return str.replace(/(\d)(\d)/g, "$1ty$2")
        .replace(/0/g, "Zero")
        .replace(/1/g, "One")
        .replace(/2/g, "Two")
        .replace(/3/g, "Three")
        .replace(/4/g, "Four")
        .replace(/5/g, "Five")
        .replace(/6/g, "Six")
        .replace(/7/g, "Seven")
        .replace(/8/g, "Eight")
        .replace(/9/g, "Nine");
}

function caster(type) {
    return  ({
        "byte": "(byte)",
        "short": "(short)",
        "int": "(int)",
        "long": "(long)",
        "float": "",
        "double": "(double)",
        "boolean": "",
        "char": "",
        "String": ""
    })[type] || "";
}

function rawValueGetter(type) {
    return  ({
        "byte": "castToNumber().getFloat()",
        "short": "castToNumber().getFloat()",
        "int": "castToNumber().getFloat()",
        "long": "castToNumber().getFloat()",
        "float": "castToNumber().getFloat()",
        "double": "castToNumber().getFloat()",
        "boolean": "castToBoolean().getBoolean()",
        "char": "getString().charAt(0)",
        "String": "getString()"
    })[type] || "getString()";
}

function equivAutoautoClass(type) {
    return ({
        "void": "AutoautoUndefined",
        "byte": "AutoautoNumericValue",
        "short": "AutoautoNumericValue",
        "int": "AutoautoNumericValue",
        "long": "AutoautoNumericValue",
        "float": "AutoautoNumericValue",
        "double": "AutoautoNumericValue",
        "boolean": "AutoautoBooleanValue",
        "char": "AutoautoString",
        "String": "AutoautoString"
    })[type] || "AutoautoUndefined";
}

function findShimableMethods(type) {
    var bodyStatements = type.bodyDeclarations;
    var methods = bodyStatements.filter(x=>x.node == "MethodDeclaration");
    var publicMethods = methods.filter(x=>x.modifiers.find(y=>y.keyword == "public"));
    
    var shimableMethods = publicMethods.filter(x=>!x.constructor && doesMethodHaveShimableArgs(x) && isTypeShimable(x.returnType2));
    
    return shimableMethods;
}

function doesMethodHaveShimableArgs(method) {
    var shimableArgs = method.parameters.filter(x=>!x.varargs && isTypeShimable(x.type));
    
    return shimableArgs.length == method.parameters.length;
}

function isTypeShimable(returnType) {
    if(returnType.node == "PrimitiveType") return true;
    else if(returnType.node == "SimpleType") return isTypeShimable(returnType.name);
    else if(returnType.node == "SimpleName" &&  returnType.identifier == "String") return true;
    else return false;
} 

function getTypeCode(type) {
    return type.primitiveTypeCode || type.name.identifier;
}

function getBasicTypeInfo(method) {
    return {
        returnType: getTypeCode(method.returnType2),
        argTypes: method.parameters.map(x=>getTypeCode(x.type)),
        argNames: method.parameters.map(x=>x.name.identifier)
    };
}

function groupMethodsIntoOverloads(methods) {
    
    var nameSet = Array.from(new Set(methods.map(x=>x.name.identifier)));

    var entries = [];
    for(var i = 0; i < nameSet.length; i++) {
        var name = nameSet[i];
        var overloads = methods.filter(x=>x.name.identifier == name);
        var overloadTypes = overloads.map(x=>getBasicTypeInfo(x));
        entries.push({ name: name, overloadTyping: overloadTypes});
    }
    return entries;
}