var cache = {};
var fs = require("fs");
var path = require("path");

module.exports = {
    readFileSync: function(f) {
        f = path.normalize(f);
        if(!cache[f]) cache[f] = fs.readFileSync(f);
        return cache[f];
    }
}