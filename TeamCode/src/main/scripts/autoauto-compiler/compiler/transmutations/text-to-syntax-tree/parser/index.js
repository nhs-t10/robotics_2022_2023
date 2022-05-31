const prattParse = require("./pratt-parse");
const tokenstream = require("./token-stream");

module.exports = function(text, file) {
    var stream = tokenstream(text, file);
    return prattParse(stream, file);
}