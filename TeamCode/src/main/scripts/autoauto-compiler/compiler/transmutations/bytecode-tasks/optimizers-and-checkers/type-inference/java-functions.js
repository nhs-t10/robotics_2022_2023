module.exports = flCacheToTypes;

function flCacheToTypes(flCache) {
    var methodTypeMap = {};
    
    for(var k in flCache) {
        if(k == "cacheVersion") continue;
        var allFileMethods = flCache[k].data.methods;
        for (var i = 0; i < allFileMethods.length; i++) {
            insertMethod(allFileMethods[i], methodTypeMap);
        }
    }
    
    return methodTypeMap;
}

function insertMethod(method, methodTypeMap) {
    //the '@0' indicates that it's a built-in
    var key = "var " + method.shimClassFunction.nameToUseInAutoauto + "@0";
    
    var returnPossibilities = [];
    var argPossibilities = [];
    var argNames = [];
    
    for(var i = 0; i < method.functionTypes.length; i++) {
        var typing = method.functionTypes[i];
        var argdata = typing.argumentTypes;
        returnPossibilities.push(aaType(argdata.returnType));
        
        if (argNames.length < argdata.argNames.length) argNames = argdata.argNames;
        
        for(var i = 0; i < typing.argCount; i++) {
            if(!argPossibilities[i]) argPossibilities.push(["undefined"]);
            argPossibilities[i].push(aaType(argdata.argTypes[i]));
        }
    }
    
    methodTypeMap[key] = assembleFunctionType(returnPossibilities, argPossibilities, argNames);
}

function aaType(t) {
    switch(t) {
        case "String": return "string";
        case "int": return "number";
        case "float": return "number";
        case "double": return "number";
        case "long": return "number";
        case "short": return "number";
        case "byte": return "number";
        case "char": return "string";
        //void is covered by the default
        default: return "undefined";
    }
}

function assembleFunctionType(returnPossibilities, argPossibilities, argNames) {
    
    var unifiedArguments = argPossibilities.map(x => typesToUnion(x));
    var returnType = typesToUnion(returnPossibilities);
    
    return {
        type: "function",
        args: unifiedArguments,
        argnames: argNames.slice(),
        return: returnType
    };
}

function typesToUnion(types) {
    var uniq = Array.from(new Set(types));
    
    if (uniq.length == 1) return uniq[0];
    else return { type: "union", types: uniq };
}