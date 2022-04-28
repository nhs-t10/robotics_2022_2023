var generate = require("./generate");
module.exports = function(seed) {
    var seedBuffer = Buffer.from(seed || randomSeed(), "hex");
    return generate(seedBuffer);
}
function randomSeed() {
    var r = "";
    while(r.length < 20) r += Math.random().toString(16).substring(2,4);
    return r;
}