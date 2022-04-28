var query = require("../query");

module.exports = {
    summary: "No Statepaths",
    run: function(ast) {

        var hasStatepath = !!query.getOneOfType(ast, "Statepath");
        if(!hasStatepath) return {
            kind: "WARNING",
            text: "Files should have at least one statepath, even if it's unlabeled.",
            fail: true
        }
    }
}