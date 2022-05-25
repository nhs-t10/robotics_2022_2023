module.exports = printHelpInfo;


function printHelpInfo(schema) {
    console.info(`Autoauto Compiler v${require("../autoauto-compiler/config").COMPILER_VERSION}
---
Usage: node autoauto-compiler [--long-flags...] [-shortflags...]

Flags:
${formatFlags(schema)}
`)
}

function formatFlags(schema) {
    var f = [];

    for(const key in schema) {
        f.push(`  --${key}=${schema[key].value} | short: ${schema[key].short.map(x=>"-"+x).join(",") || "none"}
    ${schema[key].description}`)
    }
    return f.join("\n");
}