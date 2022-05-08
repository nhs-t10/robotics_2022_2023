const query = require("../query");

module.exports = {
    summary: "Semantically useless `block`",
    run: function(ast, frontmatter) {
        
        if (frontmatter.ignorewarning_semantically_useless_block == true) return;

        var blocks = query.getAllOfType(ast, "Block");
        
        for(var i = 0; i < blocks.length; i++) {
            var stmt = blocks[i];
            var hasScopedVars = !!query.getOneOfType(stmt, "LetStatement");
            if(query.getParentOf(stmt).type == "State" && !hasScopedVars) {
                return {
                    kind: "WARNING",
                    text: `This block doesn't declare any variables, and it's not inside a conditional or control statement. Therefore, it can be removed to simplify code`,
                    original: `Add \`ignorewarning_semantically_useless_block: true\` to the frontmatter at the start of your file to remove this warning.\n` +
                        `For example,
                $
                ignorewarning_semantically_useless_block: true
                $`,
                    location: stmt.location
                }
            }
        }
    }
}