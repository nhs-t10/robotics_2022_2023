const bcMnemoms = require("./bc");

module.exports = {
    formatBc: formatBc,
    formatFlattened: formatFlattened
}

function formatFlattened(bcArr) {
    return bcArr.map(x=>formatBcInstr(x)).join("\n");
}

function formatBc(bytecode) {
    return Object.values(bytecode).map(x=>{
        var code = formatBcArray(x.code);
        var jumps = formatBcArray(x.jumps);

        return `'${x.label}':\n${code}\n${jumps}\n\n`;
    }).join("\n");
}

function formatBcArray(bcArr) {
    return bcArr.map(x=>formatBcInstr(x)).join(" ");
}

function formatBcInstr(bcInstr) {
    var c = formatBcArray(bcInstr.args);

    var lbl = "";
    if(bcMnemoms[bcInstr.code]) {
        lbl = bcMnemoms[bcInstr.code].mnemom;
    } else {
        lbl = "(" + JSON.stringify(bcInstr.__value) + ")";
    }

    return c + " " + lbl;
}