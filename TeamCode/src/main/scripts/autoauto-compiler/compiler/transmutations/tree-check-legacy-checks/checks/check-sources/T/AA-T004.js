var path = require("path");

var query = require("../query");

module.exports = {
    summary: "Unknown statepath in a `goto`",
    run: function(ast) {

        var gotoStatements = query.getAllOfType(ast, "GotoStatement");
        var statepathNames = query.getAllOfType(ast, "LabeledStatepath").map(x=>x.label);
        
        var warns = [];
        
        for(var i = 0; i < gotoStatements.length; i++) {
            var stmt = gotoStatements[i];
            if(stmt.path.type == "Identifier") {
                if(!statepathNames.includes(stmt.path.value)) {
                    warns.push({
                        kind: "WARNING",
                        text: `A \`goto\` statement wants to go to #${stmt.path.value}, but it doesn't exist.`,
                        location: stmt.location
                    });
                }
            }
        }
        return warns;
    }
}