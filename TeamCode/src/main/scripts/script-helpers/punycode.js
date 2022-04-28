var bitwiseyHelpers = require("./bitwisey-helpers.js");

module.exports = function encodeForJs(str) {
    str = str + "";
    var r = "";
    var unsentChars = [];
    for(var i = 0; i < str.length; i++) {
        var c = str.charCodeAt(i);
        if(isPermittedCode(c)) r += str[i];
        else unsentChars.push((i+1) * c);
    }
    r += "$";

    for(var i = 0; i < unsentChars.length; i++) {
        r += bitwiseyHelpers.numToB62(bitwiseyHelpers.toVarint8(unsentChars[i]));
    }
    return r;
}


function isPermittedCode(c) {
    return isNumericCode(c) || isLetterCode(c) || isUnderscoreCode(c);
}
function isNumericCode(c) {
    return c >= 0x30 && c <= 0x39;
}
function isLetterCode(c) {
    return (c >= 0x41 && c <= 0x5A) || (c >= 0x61 && c <= 0x7A);
}
function isUnderscoreCode(c) {
    return c == 0x5F;
}