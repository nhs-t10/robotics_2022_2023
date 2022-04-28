var path = require("path");

var unicode = require("../../../../script-helpers/unicode");

module.exports = function(context) {
    var fileContent = context.lastInput;
    var warning = getUnicodeWarning(fileContent);
    
    if(warning) throw warning;
    
    context.status = "pass";
}

function getUnicodeWarning(fileContent) {
    var inQuotes = false;
    for(var i = 0; i < fileContent.length; i++) {
        var char = fileContent[i];
        if(isQuote(char) && !isEscape(char)) inQuotes = !inQuotes;

        if(!inQuotes) {
            if(isIllegalChar(char)) {
                return buildWarningFor(char, i, folder, filename)
            }
        }
    }
}

function buildWarningFor(char, index, folder, filename) {
    var unicodeRecord = unicode.get(char);

    var homoglyph = unicode.getHomoglyph(char);
    var homoglyphOffer = homoglyph ? ` Try using "${homoglyph}" instead?` : "";

    return {
        kind: "WARNING",
        fail: true,
        text: `Used the unicode character ${unicodeRecord||""} (${JSON.stringify(char)}) in source code.${homoglyphOffer}`,
        sources: [{
            file: path.join(folder, filename),
            position: {
                startOffset: index,
                endOffset: index + 1
            }
        }]
    }
}
function isQuote(char) {
    return char == "'" || char == '"';
}
function isEscape(char) {
    return char == "\\";
}
function isIllegalChar(char) {
    var code = char.codePointAt(0);

    //newlines are control characters, but not illegal
    if(code == 10 || code == 13) return false;

    //the code has to be outside of The Weird Zones in ASCII.
    else return code < 32 || code > 126;
}