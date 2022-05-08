const query = require("../query");

module.exports = {
    summary: "Single-statement block",
    run: function(ast, frontmatter) {
        
        if (frontmatter.ignorewarning_single_statement_block == true) return;

        var blocks = query.getAllOfType(ast, "Block");
        
        for(var i = 0; i < blocks.length; i++) {
            var stmt = blocks[i];

            if(stmt.state.statement.length == 1) {
                return {
                    kind: "WARNING",
                    text: `This block only has 1 statement; the curly brackets can be removed.`,
                    original: `Add \`ignorewarning_single_statement_block: true\` to the frontmatter at the start of your file to remove this warning.\n` +
                    `For example,
                $
                ignorewarning_single_statement_block: true
                $`,
                    location: stmt.location
                }
            }
        }
    }
}