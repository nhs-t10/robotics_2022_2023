"use strict";

const androidStudioLogging = require("../../../../../../script-helpers/android-studio-logging");

const getBinaryOperatorResult = require("./binary-operator-result");

module.exports = verifyTypeSystem;

async function verifyTypeSystem(typeSystem, filename) {    
    let typeUnfounded = false;
    for(const typeName in typeSystem) {
        const type = typeSystem[typeName];
        const typeIsUnfounded = await verifyType(typeSystem, typeName, type, filename);
        if(typeIsUnfounded) typeUnfounded = true;
    }

    if(typeUnfounded) androidStudioLogging.sendTreeLocationMessage({
        text: "Type checking limited",
        original: `Because of the halting problem, a type system is undecidable. The type checker was unable to verify everything in this file. There may be unseen errors!`,
        kind: "BARELY_WARNING"
    }, filename)
}

async function verifyType(typeSystem, typeName, type, filename) {
    return await resolveType(typeSystem, typeName, type, {}, filename);
}

async function resolveType(typeSystem, typeName, type, visitedTypes, filename) {

    if(typeName in visitedTypes) {
        return true;
    } else {
        visitedTypes[typeName] = type.location;
    }
    if(typeof type === "string" || Array.isArray(type)) {
        delete visitedTypes[typeName];
        return;
    };

    var r;
    switch(type.type) {
        case "union": r = resolveUnion(typeSystem, typeName, type, visitedTypes, filename); 
        break;
        case "object_apply": r = resolveObjectApply(typeSystem, typeName, type, visitedTypes, filename); 
        break;
        case "?": typeSystem[typeName] = "?"; 
        break;
        case "primitive": r = resolvePrimitive(typeSystem, typeName, type); 
        break;
        case "binary_operator": r = resolveBinaryOp(typeSystem, typeName, type, visitedTypes, filename);
        break;
        case "apply": r = resolveFunctionApplication(typeSystem, typeName, type, visitedTypes, filename);
        break;
        case "object":
        case "function":
        break;

        default: console.error(type); throw "no type type " + type.type;
    }
    if(r) return await r;
    else delete visitedTypes[typeName];
    
}

async function resolveFunctionApplication(typeSystem, typeName, type, visitedTypes, filename) {
    const functionIsUnresolved = await resolveType(typeSystem, type.operand, typeSystem[type.operand], visitedTypes, filename);
    if(functionIsUnresolved) return true;

    var funcType = typeSystem[type.operand];

    if(funcType.type != "function") {
        androidStudioLogging.sendTreeLocationMessage({
            text: "Attempt to call non-function",
            original: "The type checker can't promise that this is a function. As such, calling it *may* produce errors!",
            location: type.location,
            kind: "WARNING"
        }, filename);
        return true;
    }

    const returnTypeIsUnresolved = await resolveType(typeSystem, funcType.return, typeSystem[funcType.return], visitedTypes, filename);
    if(returnTypeIsUnresolved) return true;

    typeSystem[typeName] = typeSystem[funcType.return];
    
}

async function resolveBinaryOp(typeSystem, typeName, type, visitedTypes, filename) {
    const leftIsUnresolved = await resolveType(typeSystem, type.left, typeSystem[type.left], visitedTypes, filename);
    if(leftIsUnresolved) return true;
    const rightIsUnresolved = await resolveType(typeSystem, type.right, typeSystem[type.right], visitedTypes, filename)
    if(rightIsUnresolved) return true;

    if(!type.op) {
        androidStudioLogging.sendTreeLocationMessage({
            text: "Unfounded binary operator",
            kind: "WARNING",
            location: type.location
        }, filename);
        return true;
    }

    typeSystem[typeName] = getBinaryOperatorResult(typeSystem[type.left], typeSystem[type.right], type.op, filename, type.location);
}

async function resolvePrimitive(typeSystem, typeName, type) {
    typeSystem[typeName] = type.primitive;
}

async function resolveObjectApply(typeSystem, typeName, type, visitedTypes, filename) {
    const objectIsUnresolved = await resolveType(typeSystem, type.object, typeSystem[type.object], visitedTypes, filename);
    if(objectIsUnresolved) return true;

    var objType = typeSystem[type.object];
    if(objType.type != "object") {
        androidStudioLogging.sendTreeLocationMessage({
            text: "Attempt to get property of non-table",
            original: "The type checker can't promise that this is a table. As such, getting a property *may* produce errors!\n",
            location: type.location,
            kind: "WARNING"
        }, filename);
        return true;
    } if(typeof type.key == "string") {
        var propertyTypeName = objType.properties[type.key] || objType.some || "undefined";
        
        const propertyIsUnresolved = await resolveType(typeSystem, propertyTypeName, typeSystem[propertyTypeName], visitedTypes, filename);
        if(propertyIsUnresolved) return true;

        typeSystem[typeName] = typeSystem[propertyTypeName];

    } else {
        return true;
    }
}

async function resolveUnion(typeSystem, typeName, type, visitedTypes, filename) {

    //optimize: if there's only 1 type, directly go to the first type & resolve it
    if(type.types.length == 1) {
        const t = type.types[0];
        const thisTypeUnresolved = await resolveType(typeSystem, t, typeSystem[t], visitedTypes, filename);
        if(thisTypeUnresolved) return true;
        typeSystem[typeName] = typeSystem[t];
        return false;
    }

    var u = [];
    for(const t of type.types) {
        const thisTypeUnresolved = await resolveType(typeSystem, t, typeSystem[t], visitedTypes, filename);
        if(thisTypeUnresolved) return true;

        var newT = typeSystem[t];
        if(typeof newT === "string") u.push(newT);
        else if(Array.isArray(newT)) u.push(...newT);
    }

    const s = new Set(u);

    if(s.size == 1) typeSystem[typeName] = typeSystem[s[0]];
    else typeSystem[typeName] = Array.from(s);
}

