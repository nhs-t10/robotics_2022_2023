const query = require("../query");

module.exports = {
    summary: "Empty block",
    run: function(ast, frontmatter) {
        
        if (frontmatter.ignorewarning_empty_block == true) return; 

        var blocks = query.getAllOfType(ast, "Block");
        
        for(var i = 0; i < blocks.length; i++) {
            var stmt = blocks[i];

            if(stmt.state.statement.length == 0) {
                return {
                    kind: "WARNING",
                    text: `This block is empty; consider replacing it with a \`pass\` statement.`,
                    original: `Add \`ignorewarning_empty_block: true\` to the frontmatter at the start of your file to remove this warning.\n` +
                        `For example,
                $
                ignorewarning_empty_block: true
                $`,
                    location: stmt.location
                }
            }
        }
    }
}