module.exports = function recursiveDescentGrabAllOfType(ast, type) {
    var children = getAstChildren(ast);

    var directChildrenOfType = children.filter(x=>x.type == type);

    var allChildrenOfType = directChildrenOfType;
    for(var i = 0; i < children.length; i++) {
        var childSearch = recursiveDescentGrabAllOfType(children[i], type);
        allChildrenOfType = allChildrenOfType.concat(childSearch);
    }

    return allChildrenOfType;
}

function getAstChildren(ast) {
    var c = Object.values(ast)
        .filter(x=>typeof x === "object")
        .flat()
        .filter(x => x && typeof x.type === "string");

    return c;
}