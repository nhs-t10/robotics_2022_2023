var qCache = new Map();
var parentCache = new Map();
var vScopeCache = new Map();


module.exports = {
    getOneOfType: function(ast, type) {
        return cachedGetAllOfType(ast, type, 1)[0];
    }, 
    getAllOfType: function(ast, type) {
        return cachedGetAllOfType(ast, type, Infinity);
    },
    getVariableScope: function(ast, node) {
        if(vScopeCache.has(node)) return vScopeCache.get(node);
        
        addVariableScopes(ast);

        return vScopeCache.get(node);
    },
    getParentOf: getParentOf
}

function getParentOf(ast) {
    return parentCache.get(ast);
}

function addVariableScopes(ast) {
    var parentScope;

    var parent = getParentOf(ast);
    //if this is the root parent, add it to the thing
    if(parent === undefined) vScopeCache.set(ast, {});
    else parentScope = vScopeCache.get(parent);

    //for these guys, make a new scope
    if(ast.type == "FunctionLiteral" || ast.type == "FunctionDefStatement" || ast.type == "Block") {
        vScopeCache.set(ast, newScopeRecord(parentScope));
    }
    //and for others, use the parent's scope
    else {
        vScopeCache.set(ast, parentScope);
    }

    var scope = vScopeCache.get(ast);

    if(ast.type == "LetStatement" && ast.variable.type=="Identifier") {
        scope.variables[ast.variable.value] = {
            definedBy: ast
        }
    }
    

}

function newScopeRecord(p) {
    return {
        parent: p,
        variables: {}
    }
}

function cachedGetAllOfType(ast, type, limit) {
    var astCache = qCache.get(ast);
    if(!astCache) astCache = {};
    if(!astCache[type]) astCache[type] = {};

    if(astCache[type][limit] || astCache[type][Infinity]) {
        //Infinity items means that it's a match for this one, too
        if(limit != Infinity) astCache[type][limit] = astCache[type][limit] || astCache[type][Infinity]
    }
    else {
        astCache[type][limit] = recursiveDescentGrabAllOfType(ast, type, limit);
    }

    qCache.set(ast, astCache);

    return astCache[type][limit];
}

function recursiveDescentGrabAllOfType(ast, type, limit) {
    var children = getAstChildren(ast);

    var directChildrenOfType = children.filter(x=>x.type == type);

    limit -= directChildrenOfType.length;

    var allChildrenOfType = directChildrenOfType;
    for(var i = 0; i < children.length && limit > 0; i++) {
        var childSearch = recursiveDescentGrabAllOfType(children[i], type, limit);
        limit -= childSearch.length;
        allChildrenOfType = allChildrenOfType.concat(childSearch);
    }

    return allChildrenOfType;
}

function getAstChildren(ast) {
    var c = Object.values(ast)
        .filter(x=>typeof x === "object")
        .flat()
        .filter(x => x && typeof x.type === "string");

    c.forEach(x=>parentCache.set(x, ast));

    return c;
}