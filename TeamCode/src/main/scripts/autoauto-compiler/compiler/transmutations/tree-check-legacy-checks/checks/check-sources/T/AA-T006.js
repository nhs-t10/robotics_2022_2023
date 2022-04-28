var path = require("path");

var query = require("../query");

module.exports = {
    summary: "Single-statement block",
    run: function(ast) {

        var blocks = query.getAllOfType(ast, "Block");
        
        for(var i = 0; i < blocks.length; i++) {
            var stmt = blocks[i];

            if(stmt.state.statement.length == 1) {
                return {
                    kind: "WARNING",
                    text: `This block only has 1 statement; the curly brackets can be removed.`,
                    location: stmt.location
                }
            }
        }
    }
}