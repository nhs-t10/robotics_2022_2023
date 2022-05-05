var path = require("path");

var directory = __dirname.split(path.sep);
var srcDirectory = directory.slice(0, directory.indexOf("src") + 1).join(path.sep);

var makePhoto = require("./create-png-from-hash.js");

var now = new Date().toISOString().replace(/[^\d-]/g, "-")

var randomPixels = createRandomHash();

for(var i = 0; i < 18; i++) {
    makePhoto("test-" + now + "-" + i, randomPixels, i);
}


function createRandomHash() {
    var pixelcount = Math.ceil(Math.random() * 15);

    var result = "";
    for(var i = 0; i < pixelcount; i++) {
        result += Math.random().toString(16).substring(2, 2 + 6);
    }
    return result;
}