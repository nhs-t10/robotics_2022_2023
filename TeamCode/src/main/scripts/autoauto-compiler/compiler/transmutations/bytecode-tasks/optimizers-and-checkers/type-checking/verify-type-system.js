"use strict";

const { type } = require("os");
/**
 * @typedef {Object.<string, TypeRecord>} TypeSystem
 */

/**
 * @typedef {string} typeId
 */

/**
 * @typedef {UnionType | PrimitiveType | FunctionType | FunctionApplyType | BinaryOperatorType | ObjectApplyingType | UnknownType | TableType} TypeRecord
 */

/**
 * @typedef {object} UnionType 
 * @property {"union"} type
 * @property {typeId[]} types
 * @property {import("../../../text-to-syntax-tree/parser").Location?} location
 */

/**
 * @typedef {object} PrimitiveType
 * @property {"primitive"} type
 * @property {string} primitive
 * @property {undefined} location
 */

/**
 * @typedef {object} FunctionType
 * @property {"function"} type
 * @property {typeId[]} args
 * @property {typeId} varargs
 * @property {string[]} argnames
 * @property {typeId} return
 * @property {import("../../../text-to-syntax-tree/parser").Location?} location
 */

/**
 * @typedef {object} FunctionApplyType
 * @property {"apply"} type
 * @property {typeId[]} positionalArguments
 * @property {Object.<string, typeId>} namedArguments
 * @property {typeId} operand
 * @property {import("../../../text-to-syntax-tree/parser").Location} location
 */

/**
 * @typedef {object} BinaryOperatorType
 * @property {"binary_operator"} type
 * @property {"+"|"-"|"/"|"*"|"**"|"^"|"=="|">="|"<="|"<"|">"|"!="} op
 * @property {typeId} left
 * @property {typeId} right
 * @property {import("../../../text-to-syntax-tree/parser").Location} location
 * 
 */

/**
 * @typedef {object} ObjectApplyingType
 * @property {"object_apply"} type
 * @property {typeId} object
 * @property {string | undefined} key
 * @property {import("../../../text-to-syntax-tree/parser").Location} location
 */

/**
 * @typedef {object} UnknownType
 * @property {"?"} type
 * @property {import("../../../text-to-syntax-tree/parser").Location | undefined} location
 */

/**
 * @typedef {object} TableType 
 * @property {"object"} type
 * @property {typeId} some
 * @property {Object.<string, typeId>} properties
 * @property {import("../../../text-to-syntax-tree/parser").Location?} location
 */

const androidStudioLogging = require("../../../../../../script-helpers/android-studio-logging");

const getBinaryOperatorResult = require("./binary-operator-result");
const { formatType, formatLocation, shortRelativeFormatLocation } = require("./format-types");

module.exports = verifyTypeSystem;

/**
 * 
 * @param {TypeSystem} typeSystem
 * @param {string} filename
 */
async function verifyTypeSystem(typeSystem, filename) {

    /** @type {Map<TypeRecord, TypeRecord[] | undefined>} */
    const resolutionCache = new Map();

    let typeUnfounded = false;
    for (const typeName in typeSystem) {
        if(typeName == "*") continue;
        
        const typeIsUnfounded = await verifyType(typeSystem, typeName, resolutionCache, filename);
        if (typeIsUnfounded) typeUnfounded = true;
    }

    if (typeUnfounded) androidStudioLogging.sendTreeLocationMessage({
        text: "Type checking limited",
        original: `Because of the halting problem, a type system is undecidable. The type checker was unable to verify everything in this file. There may be unseen errors!`,
        kind: "BARELY_WARNING"
    }, filename);
}

/**
 * 
 * @param {TypeSystem} typeSystem 
 * @param {typeId} typeName 
 * @param {Map<TypeRecord, TypeRecord[] | undefined>} resolutionCache
 * @param {string} filename
 * @returns {Promise<boolean>} true if the type is unfounded or unresolvable
 */
async function verifyType(typeSystem, typeName, resolutionCache, filename) {
    return await resolveType(typeSystem, typeName, [], resolutionCache, filename) === undefined;
}

/**
 * 
 * @param {TypeSystem} typeSystem 
 * @param {typeId} typeName 
 * @param {TypeRecord[]} visitedTypes 
 * @param {Map<TypeRecord, TypeRecord[] | undefined>} resolutionCache
 * @param {string} filename
 * @returns {Promise<TypeRecord[] | undefined>}
 */
async function resolveType(typeSystem, typeName, visitedTypes, resolutionCache, filename) {
    const type = typeSystem[typeName];

    if (resolutionCache.has(type)) return resolutionCache.get(type);

    if (visitedTypes.includes(type)) {
        return await recoverFromRecursiveData(typeSystem, type, visitedTypes, filename);
    }

    visitedTypes.push(type);

    const res = await simpleResolveTypeWithSpecific(typeSystem, typeName, type, visitedTypes, resolutionCache, filename);
    
    visitedTypes.pop();
    
    resolutionCache.set(type, res);
    return res;
}

/**
 * 
 * @param {TypeSystem} typeSystem 
 * @param {TypeRecord} type 
 * @param {TypeRecord[]} visitedTypes 
 * @param {string} filename
 * @returns {Promise<TypeRecord[] | undefined>}
 */
async function recoverFromRecursiveData(typeSystem, type, visitedTypes, filename) {
    
    /** @type {Set<TypeRecord>} */
    const bases = new Set();
    for(const vType of visitedTypes) {
        bases.add(...findUnionBottomTypes(typeSystem, vType));
    }
    
    const loopingMathResult = findRecursionIsFromLoopingMath(bases, typeSystem);
    if(loopingMathResult) return loopingMathResult;
    
    
    return logRecursionWarning(typeSystem, type, visitedTypes, filename);
}

/**
 * 
 * @param {TypeSystem} typeSystem 
 * @param {UnionType} unionType 
 * @returns {Set<TypeSystem>}
 */
function findUnionBottomTypes(typeSystem, unionType) {
    const scannedUnions = new Set();
    const unionsToScan = [unionType];
    
    let results = new Set();
    
    for (let i = 0; i < unionsToScan.length; i++) {
        const union = unionsToScan[i];
        
        if(scannedUnions.has(union)) continue;
        else scannedUnions.add(union);
        
        const subtypeNames = union.types ? union.types : 
            union.left ? [union.left, union.right] : 
            []
        
        for (const typeName of subtypeNames) {
            const subtype = typeSystem[typeName];
            
            if (subtype.type == "union" || subtype.type == "binary_operator") {
                unionsToScan.push(subtype);
            } else {
                results.add(subtype);
            }
        }
    }
    
    return results;
}

/**
 * 
 * @param {Set<TypeRecord>} bases 
 * @param {TypeSystem} typeSystem
 */
function findRecursionIsFromLoopingMath(bases, typeSystem) {
    const primitives = new Set();
    
    for (const type of bases) {
        if (type.type == "primitive") primitives.add(type.primitive);
        else return undefined;
    }
    
    if (primitives.has("number")) {
        if (primitives.size == 1) return [ typeSystem["number"] ];
        
        if(primitives.size == 2 && primitives.has("undefined")) {
            return [typeSystem["number"], typeSystem["undefined"]];
        }
    }
    
} 

/**
 * 
 * @param {TypeSystem} typeSystem 
 * @param {TypeRecord} type 
 * @param {TypeRecord[]} visitedTypes 
 * @param {string} filename
 * @returns {undefined}
 */
function logRecursionWarning(typeSystem, type, visitedTypes, filename) {
    androidStudioLogging.sendTreeLocationMessage({
        text: "Recursive data",
        original: "This is a recursive type (a type that contains itself). This could be because of a variety of causes:"
            + "\n - A variable that depends on itself"
            + "\n - A table that holds values the same shape as itself"
            + "\n - Odd program structuring (files which depend on themselves)"
            + "\n\nThis isn't a bad thing; recursive data types are helpful. They're just harder to check automatically. Be sure that it's sound, and you'll be okay!"
            + "\n\n[DEBUG] Recursive set:\n    " + Array.from(visitedTypes).concat([type]).map(x => formatType(x, typeSystem) + "\t" + shortRelativeFormatLocation(x.location)).join("\n    "),
        kind: "WARNING",
        location: type.location
    }, filename);
    return undefined;
}

/**
 * 
 * @param {TypeSystem} typeSystem 
 * @param {typeId} typeName 
 * @param {TypeRecord} type 
 * @param {Set<TypeRecord>} visitedTypes 
 * @param {Map<TypeRecord, TypeRecord[] | undefined>} resolutionCache
 * @param {string} filename
 * @returns {Promse<TypeRecord[] | undefined>}
 */
async function simpleResolveTypeWithSpecific(typeSystem, typeName, type, visitedTypes, resolutionCache, filename) {
    switch (type.type) {
        case "union": return await resolveUnion(typeSystem, typeName, type, visitedTypes, resolutionCache, filename);
        case "object_apply": return await resolveObjectApply(typeSystem, typeName, type, visitedTypes, resolutionCache, filename);
        case "binary_operator": return await resolveBinaryOp(typeSystem, typeName, type, visitedTypes, resolutionCache, filename);
        case "apply": return await resolveFunctionApplication(typeSystem, typeName, type, visitedTypes, resolutionCache, filename);

        case "function": return await resolveFunction(typeSystem, typeName, type, visitedTypes, resolutionCache, filename);
        
        case "primitive": return [type];
        case "object": return [type];

        
        case "?": return logUnknownType(typeName, type);

        default: console.error(type); throw "no type type " + type.type;
    }
}

/**
 * 
 * @param {string} typeName 
 * @param {UnknownType} type 
 */
function logUnknownType(typeName, type) {
    if(type.location !== undefined) {
        
        const typeIsFromVariable = typeName.startsWith("var ");
        
        const text = typeIsFromVariable ? "Unknown Variable" : "Unknown Typing";
        const explanation = typeIsFromVariable ? "The variable '" + 
            typeName.substring(typeName.indexOf("-") + 1, typeName.indexOf("@")) + "' couldn't be resolved"
            : "There was an unresolved type. Unfortunately, that's all we know.\n\nDEBUG: `typeName`: " + typeName;
        
        androidStudioLogging.sendTreeLocationMessage({
            text: text,
            original: explanation,
            location: type.location,
            kind: "WARNING"
        })
    }
    
    return undefined;
}


/**
 * 
 * @param {TypeSystem} typeSystem 
 * @param {typeId} typeName 
 * @param {FunctionType} type 
 * @param {TypeRecord[]} visitedTypes 
 * @param {Map<TypeRecord, TypeRecord[] | undefined>} resolutionCache
 * @param {string} filename
 * @returns {Promise<TypeRecord[] | undefined>}
 */
async function resolveFunction(typeSystem, typeName, type, visitedTypes, resolutionCache, filename) {
    
    const returnTypes = await resolveType(typeSystem, type.return, visitedTypes, resolutionCache, filename);
    if (returnTypes === undefined) return undefined;
    
    const returnId = insertReturnTypes(typeSystem, returnTypes, typeName);
    
    
    return [{
        type: "function",
        argnames: type.argnames,
        location: type.location,
        return: returnId,
        varargs: type.varargs,
        args: type.args
    }];
}

/**
 * 
 * @param {TypeSystem} typeSystem 
 * @param {TypeRecord[]} types 
 * @param {string} typeName 
 * @returns {string}
 */
function insertReturnTypes(typeSystem, types, typeName) {
    const typeIds = [];
    const resultId = typeName + "->";
    
    if(types.length == 1) {
        typeSystem[resultId] = types[0];
        return resultId;
    }
    
    for(let i = types.length - 1; i >= 0; i--) {
        const type = types[i];
        if(type.type == "primitive") typeIds.push(type.primitive);
        else if(type.type == "union") typeIds.push(...type.types);
        else {
            const id = resultId + "[" + i + "]";
            typeSystem[id] = type;
            typeIds.push(id);
        }
    }
    
    if(typeIds.length == 1) return typeIds[0];
    
    const t = {
        type: "union",
        location: typeSystem[typeName].location,
        types: typeIds
    };
    typeSystem[resultId] = t;
    return resultId;
}

/**
 * 
 * @param {TypeSystem} typeSystem 
 * @param {typeId} typeName 
 * @param {FunctionApplyType} type 
 * @param {TypeRecord[]} visitedTypes 
 * @param {Map<TypeRecord, TypeRecord[] | undefined>} resolutionCache
 * @param {string} filename
 * @returns {Promise<TypeRecord[] | undefined>}
 */
async function resolveFunctionApplication(typeSystem, typeName, type, visitedTypes, resolutionCache, filename) {
    const appliedFunction = await resolveType(typeSystem, type.operand, visitedTypes, resolutionCache, filename);
    if (appliedFunction === undefined) return appliedFunction;

    if (allAreOfDefiniteType(appliedFunction, "function") == false) {
        androidStudioLogging.sendTreeLocationMessage({
            text: "Attempt to call non-function",
            original: "The type checker can't promise that the given value is a function. Trying to call a non-function can cause problems.\n" +
            "Expected `function(any...) -> any`; got `" + formatType(appliedFunction, typeSystem) + "`",
            location: type.location,
            kind: "WARNING"
        });
        return undefined;
    }
    
    for(const argumentName in type.namedArguments) {
        if(allHaveArgumentNamed(appliedFunction, argumentName) == false) {
            androidStudioLogging.sendTreeLocationMessage({
                text: "Function called with missing named argument",
                original: "The type checker can't promise that this function has an argument named `" + argumentName + "`. " +
                    "Trying to call this function can cause problems.\n" +
                    "Expected `function(..." + argumentName + ":any...) -> any`; got `" + formatType(appliedFunction, typeSystem) + "`",
                location: type.location,
                kind: "WARNING"
            });
            return undefined;
        }
    }

    const returnTypeUnionName = unionizeReturnType(appliedFunction, typeSystem, type.location);
    
    typeSystem[typeName] = typeSystem[returnTypeUnionName];

    return await resolveType(typeSystem, returnTypeUnionName, visitedTypes, resolutionCache, filename);
}

/**
 * 
 * @param {FunctionType[]} functions
 * @param {string} name
 * @returns {boolean}
 */
function allHaveArgumentNamed(functions, name) {
    for(const func of functions) {
        if(func.argnames.includes(name) == false) return false;
    }
    return true;
}

/**
 * 
 * @param {FunctionType[]} functions 
 * @param {TypeSystem} typeSystem 
 * @param {import("../../../text-to-syntax-tree/parser").Location} Location
 * @return {typeId}
 */
function unionizeReturnType(functions, typeSystem, location) {
    const returns = Array.from(new Set(functions.map(x => x.return)));
    const returnName = returns.join("|");

    if (typeSystem[returnName] === undefined) typeSystem[returnName] = {
        type: "union",
        types: returns,
        location: location
    }

    return returnName;
}

/**
 * 
 * @param {TypeRecord[]} types 
 * @param {"object" | "function" | "union" | "primitive" | "apply" | "binary_operator" | "object_apply" | "?"} neededTypeType
 */
function allAreOfDefiniteType(types, neededTypeType) {
    for (const type of types) {
        if (type.type !== neededTypeType) return false;
    }
    return true;
}

/**
 * 
 * @param {TypeSystem} typeSystem 
 * @param {typeId} typeName 
 * @param {BinaryOperatorType} type 
 * @param {Set<TypeRecord>} visitedTypes 
 * @param {Map<TypeRecord, TypeRecord[] | undefined>} resolutionCache
 * @param {string} filename
 * @returns {Promise<TypeRecord[] | undefined>}
 */
async function resolveBinaryOp(typeSystem, typeName, type, visitedTypes, resolutionCache, filename) {
    const leftType = await resolveType(typeSystem, type.left, visitedTypes, resolutionCache, filename);
    if (leftType === undefined) return leftType;
    const rightType = await resolveType(typeSystem, type.right, visitedTypes, resolutionCache, filename)
    if (rightType === undefined) return rightType;

    if (!type.op) {
        androidStudioLogging.sendTreeLocationMessage({
            text: "Unfounded binary operator",
            kind: "WARNING",
            location: type.location
        });
        return undefined;
    }

    const resultType = getBinaryOperatorResult(type, leftType, rightType, typeSystem);

    return [resultType]
}

/**
 * 
 * @param {TypeSystem} typeSystem 
 * @param {typeId} typeName
 * @param {TypeRecord} type
 * @param {TypeRecord[]} visitedTypes
 * @param {Map<TypeRecord, TypeRecord[] | undefined>} resolutionCache
 * @param {string} filename
 * @returns {Promise<TypeRecord[] | undefined>}
 */
async function resolveObjectApply(typeSystem, typeName, type, visitedTypes, resolutionCache, filename) {

    var objTypes = await resolveType(typeSystem, type.object, visitedTypes, resolutionCache, filename);
    if (objTypes === undefined) return objTypes;



    if (allAreOfDefiniteType(objTypes, "object") === false) {
        androidStudioLogging.sendTreeLocationMessage({
            text: "Attempt to get property of non-table",
            original: "The type checker can't promise that this is a table: only `" + formatType(objTypes, typeSystem) + "`. As such, getting a property *may* produce errors!\n",
            location: type.location,
            kind: "WARNING"
        });
        return undefined;
    }

    let resultType = unionizeProperties(objTypes, type.key, typeSystem, type.location);


    if (resultType === undefined) {
        androidStudioLogging.sendTreeLocationMessage({
            text: "Attempt to access unknown table property",
            original: "The type checker can't promise that this table has the " +
                (typeof type.key === "string" ? "`" + type.key + "`" : "given") +
                " property. Getting this property *may* produce errors!\n" +
                "This table is " + formatType(objTypes, typeSystem),
            location: type.location,
            kind: "WARNING"
        });

        return undefined;
    }
    
    typeSystem[typeName] = typeSystem[resultType];

    return await resolveType(typeSystem, resultType, visitedTypes, resolutionCache, filename);
}

/**
 * 
 * @param {TableType[]} types 
 * @param {string | undefined} propertyName 
 * @param {TypeSystem} typeSystem
 * @param {import("./binary-operator-result").Location} location
 * @return {typeId | undefined}
 */
function unionizeProperties(types, propertyName, typeSystem, location) {
    const typeIds = new Set();
    for (const type of types) {
        if (propertyName === undefined) typeIds.add(type.some);
        else if (propertyName in type.properties) typeIds.add(type.properties[propertyName]);
        else return undefined;
    }

    const typeIdArray = Array.from(typeIds);
    const typeName = typeIdArray.join("|");

    if (typeSystem[typeName] === undefined) typeSystem[typeName] = {
        type: "union",
        types: typeIdArray,
        location: location
    };

    return typeName;

}

/**
 * 
 * @param {TypeSystem} typeSystem 
 * @param {string} typeName 
 * @param {UnionType} type 
 * @param {TypeRecord[]} visitedTypes
 * @param {Map<TypeRecord, TypeRecord[] | undefined>} resolutionCache
 * @param {string} filename
 * @returns {Promise<TypeRecord[] | undefined>}
 */
async function resolveUnion(typeSystem, typeName, type, visitedTypes, resolutionCache, filename) {

    //optimize: if there's 0 types, add 'undefined'
    if (type.types.length == 0) {
        return [typeSystem["undefined"]];
    }

    /** @type {TypeRecord[]} */
    var u = new Set();
    for (const typeId of type.types) {
        const unionElement = await resolveType(typeSystem, typeId, visitedTypes, resolutionCache, filename);
        if (unionElement === undefined) return unionElement;


        u.add(...unionElement);
    }

    const types = Array.from(u);

    return types;
}