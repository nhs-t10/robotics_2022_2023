var homoglyphs = require("./homoglyphs.json");
var table = require("./table.json");

module.exports = {
    get: function(char) {
        var code = padCode(char.codePointAt(0).toString(16));
        return table[code];
    },
    getHomoglyph(char) {
        return homoglyphs[char];
    }
}

function padCode(code) {
    while(code.length < 4) code = "0" + code;
    return "0x" + code;
}