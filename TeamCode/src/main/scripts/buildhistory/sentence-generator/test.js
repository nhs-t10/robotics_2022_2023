var test = require("./index");
var seed = randomSeed();

console.warn(seed);
console.warn(test(seed));

function randomSeed() {
    var r = "";
    while(r.length < 20) r += Math.random().toString(16).substring(2,4);
    return r;
}