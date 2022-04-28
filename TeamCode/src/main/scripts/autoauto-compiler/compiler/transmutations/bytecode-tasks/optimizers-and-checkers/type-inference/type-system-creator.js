
var initial = require("./initial-assumptions");
var javaFuncs = require("./java-functions");

module.exports = function (javaManagers) {
    var tsMap = {};
    var anonymousInnerTypeCounter = { k: 0, inverseMap: new Map() };
    
    restoreFromMappings(tsMap, initial, anonymousInnerTypeCounter);
    restoreFromMappings(tsMap, javaFuncs(javaManagers), anonymousInnerTypeCounter);
    
    return {
        __t: tsMap,
        getKeyOf: function(type) {
            return anonymousInnerTypeCounter.inverseMap.get(type);
        },
        getType: function(key) {
            return tsMap[getType(tsMap, key, anonymousInnerTypeCounter)];  
        },
        upsertType: function (mappingTitle, uninterpertedMapping, location) {
            if(!location) throw new Error("bad location!");
            
            return upsertInterpretMapping(tsMap, mappingTitle, uninterpertedMapping, anonymousInnerTypeCounter, location);
        },
        makeAnonymousTypeName: function() {
            return makeAnonymousTypeName(anonymousInnerTypeCounter);
        }
    };
}

function restoreFromMappings(cannonical, historical, a) {
    for(var k in historical) {
        upsertInterpretMapping(cannonical, k, historical[k], a);
    }
}

function upsertInterpretMapping(cannonical, mappingTitle, uninterpertedMapping, a, loc) {
    mappingTitle += "";

    var original = cannonical[getType(cannonical, mappingTitle, a)];

    if(uninterpertedMapping == undefined) throw new Error("Undefined type mapping");
    
    var interperted = interpertMapping(cannonical, uninterpertedMapping, a);
    if(original.type == "?") {
        Object.assign(original, interperted);
        original.location = loc;
    } else {
        mergeTypes(original, interperted, cannonical, a);
    }
    
    return original;
}

function mergeTypes(original, interperted, cannonical, a) {
    original = descendAlias(original, cannonical);
    interperted = descendAlias(interperted, cannonical);

    if("primitive" in original) return;

    if(original.type != interperted.type) {
        Object.assign(original, interperted);
    } else {
        switch(original.type) {
            case "union": original.types = original.types.concat(interperted.types);
            break;
            case "object": mergeObjects(original, interperted, cannonical, a);
            break;
            default: throw "bad merge of " + original.type;
        }
    }
}

function mergeObjects(original, interperted, cannonical, a) {
    Object.assign(original.properties, interperted.properties);

    if(original.some != "undefined" && interperted.some != "undefined") {
        original.some = getType(cannonical, { type: "union", types: [original.some, interperted.some] }, a);
    } else if(interperted.some != "undefined") {
        original.some = interperted.some;
    }
}

function descendAlias(t, cannonical) {
    if(t.type == "union" && t.types.length == 1) {
        return descendAlias(cannonical[t.types[0]], cannonical);
    }
    else {
        return t;
    }
}

function makeAnonymousTypeName(anonymousInnerTypeCounter) {
    if (anonymousInnerTypeCounter === undefined) throw new Error("no aitc");
    var anonKey = "$$anonymous inner type #" + anonymousInnerTypeCounter.k;
    anonymousInnerTypeCounter.k++;
    return anonKey;
}

function getType(cannonical, typeName, a, loc) {
    if(typeof typeName !== "string") {
        var type = typeName;
        var anonKey = makeAnonymousTypeName(a);
        upsertInterpretMapping(cannonical, anonKey, type, a, loc);
        return anonKey;
    }
    
    if(!cannonical[typeName]) {
        cannonical[typeName] = { type: "?" };
        a.inverseMap.set(cannonical[typeName], typeName);
    }
    return typeName;
}

function interpertMapping(cannonical, mapping, a) {
    
    switch(mapping.type) {
        case "primitive": return Object.assign({}, mapping);
        
        case "object_apply": return resolveObjectApply(cannonical, mapping, a);
        case "alias": return resolveAliasType(cannonical, mapping, a);
        case "union": return resolveUnionType(cannonical, mapping, a);
        case "object": return resolveObjectType(cannonical, mapping, a);
        case "function": return resolveFunctionType(cannonical, mapping, a);
        case "bytecode": return resolveBytecodeType(cannonical, mapping, a);
        case "binary_operator": return resolveBinaryOperatorType(cannonical, mapping, a);
        case "apply": return resolveFunctionApplicationType(cannonical, mapping, a);
    }
    console.error(mapping);
    throw new Error("Unknown type-mapping variant '" + mapping.type + "'");
}

function resolveFunctionApplicationType(cannonical, mapping, a) {
    var nArgs = {};
    for(var n in mapping.namedArguments) nArgs[n] = getType(cannonical, mapping.namedArguments[n], a);
    
    var pArgs = mapping.positionalArguments.map(x => getType(cannonical, x, a));
    
    return {
        type: "apply",
        positionalArguments: pArgs,
        namedArguments: nArgs,
        operand: getType(cannonical, mapping.operand, a)
    };
}

function resolveBinaryOperatorType(cannonical, bOpMapping, a) {
    return {
        type: "binary_operator",
        op: bOpMapping.op + "",
        left: getType(cannonical, bOpMapping.left, a),
        right: getType(cannonical, bOpMapping.right, a)
    }
}

function resolveAliasType(cannonical, aliasMapping, a) {
    return {
        type: "union",
        types: [getType(cannonical, aliasMapping.typeTo, a)]
    }
}

function resolveBytecodeType(cannonical, bcMapping, a) {
    return {
        type: "bytecode",
        pop: bcMapping.pop.map(x => getType(cannonical, x, a)),
        push: bcMapping.push.map(x => getType(cannonical, x, a))
    }
}

function resolveUnionType(cannonical, unionMapping, a) {
    return {
        type: "union",
        types: unionMapping.types.map(x => getType(cannonical, x, a))
    };
}

function resolveObjectApply(cannonical, mapping, a) {
    return {
        type: "object_apply",
        object: getType(cannonical, mapping.object, a),
        key: mapping.key
    };
}

function resolveObjectType(cannonical, mapping, a) {
    var r = {
        type: "object",
        some: getType(cannonical, mapping.some || "undefined", a),
        properties: {}
    };
    
    for(var k in mapping.properties) {
        r.properties[k] = getType(cannonical, mapping.properties[k], a);
    }
    
    return r;
}

function resolveFunctionType(cannonical, functionMapping, a) {
    
    return {
        type: "function",
        args: functionMapping.args.map(x => getType(cannonical, x, a)),
        varargs: getType(cannonical, functionMapping.varargs || "undefined", a),
        argnames: (functionMapping.argnames || []).slice(),
        return: getType(cannonical, functionMapping.return || "undefined", a)
    }
}