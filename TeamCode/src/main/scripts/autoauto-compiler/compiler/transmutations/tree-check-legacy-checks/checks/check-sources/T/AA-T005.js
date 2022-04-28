var path = require("path");

var query = require("../query");

module.exports = {
    summary: "Semantically useless `block`",
    run: function(ast) {

        var blocks = query.getAllOfType(ast, "Block");
        
        for(var i = 0; i < blocks.length; i++) {
            var stmt = blocks[i];
            var hasScopedVars = !!query.getOneOfType(stmt, "LetStatement");
            if(query.getParentOf(stmt).type == "State" && !hasScopedVars) {
                return {
                    kind: "WARNING",
                    text: `This block doesn't declare any variables, and it's not inside a conditional or control statement. Therefore, it can be removed to simplify code`,
                    location: stmt.location
                }
            }
        }
    }
}