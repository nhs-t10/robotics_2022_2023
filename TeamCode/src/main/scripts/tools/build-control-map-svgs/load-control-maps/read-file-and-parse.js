var fs = require("fs");
var javaparser = require("../../../script-helpers/javaparser/parser");

module.exports = function(file) {
    var fContent = fs.readFileSync(file).toString();
    try {
        var fContentStripped = fContent.replace(/\/\/.+/g, "");
        var parseTree = javaparser.parse(fContentStripped);

        return structureDefs(
            findControlDefinitions(
                findInitMethod(
                    parseTree.types[0]
                )
            )
        );
    } catch(e) {
        console.error(e);
    }
}

function structureDefs(callsToRegisterInput) {
    return Object.fromEntries(callsToRegisterInput.map(x=>regInputCallParseTreeToControlEntry(x)));
}

function regInputCallParseTreeToControlEntry(callTree) {
    var args = callTree.expression.arguments;
    var name = JSON.parse(args[0].escapedValue);

    var otherArgs = args.slice(1);
    var basicStruct;
    if(otherArgs.length == 1) basicStruct = nodeCreatorToDetailedStructure(otherArgs[0]);
    else basicStruct = {type: "MultiInput", children: otherArgs.map(x=>nodeCreatorToDetailedStructure(x)) }

    return [name, basicStruct];
}

function nodeCreatorToDetailedStructure(nodeCreatorTree) {
    var basic = nodeCreatorToBasicStructure(nodeCreatorTree);

    return basicToDetailedControlStructure(basic);
}

function basicToDetailedControlStructure(basicStruct) {
    if(basicStruct.type == "Button" || basicStruct.type == "Joystick") {
        return { type: "Button", button: basicStruct.args[0] }
    }

    if(basicStruct.args) basicStruct.args = basicStruct.args.map(x=>basicToDetailedControlStructure(x));
    
    return basicStruct;
}

function nodeCreatorToBasicStructure(nodeCreatorTree) {
    if(nodeCreatorTree.node != "ClassInstanceCreation") {
        if(nodeCreatorTree.node == "StringLiteral") return JSON.parse(nodeCreatorTree.escapedValue);
        else if(nodeCreatorTree.node == "NumberLiteral") return +nodeCreatorTree.token.replace(/[_f]/g, "");
        else if(nodeCreatorTree.node == "PrefixExpression"
            && nodeCreatorTree.operator == "-") return -1 * nodeCreatorToBasicStructure(nodeCreatorTree.operand);
    }

    var tName = nodeCreatorTree.type.name.identifier.replace(/Node$/, "");

    return {
        type: tName,
        args: nodeCreatorTree.arguments.map(x=>nodeCreatorToBasicStructure(x))
    }
}

function findControlDefinitions(initMethod) {
    var statements = initMethod.body.statements;
    
    var inputManagerName = statements.find(x=>x.node == "ExpressionStatement" 
        && x.expression.node == "Assignment"
        && x.expression.operator == "=" 
        && x.expression.rightHandSide.node == "ClassInstanceCreation"
        //&& x.expression.rightHandSide.type.node == "SimpleType"
        && x.expression.rightHandSide.type.name.identifier == "InputManager")
    .expression.leftHandSide.identifier;
    
    var registerInputCalls = statements
    .filter(x=>x.node == "ExpressionStatement" 
        && x.expression.node == "MethodInvocation"
        && x.expression.expression.identifier == inputManagerName
        && x.expression.name.identifier == "registerInput");

    return registerInputCalls;
}

function findInitMethod(type) {
    var body = type.bodyDeclarations;
    return body.find(x=>x.node == "MethodDeclaration" && x.name.identifier == "init");
}