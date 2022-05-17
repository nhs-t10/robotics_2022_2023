const prattParse = require("./pratt-parse");
const tokenstream = require("./token-stream");

module.exports = function(text) {
    var stream = tokenstream(text);
    return prattParse(stream);
}