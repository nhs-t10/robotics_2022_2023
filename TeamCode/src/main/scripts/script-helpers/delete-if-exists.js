var fs = require("fs");
var path = require("path");

module.exports = function clearDirectory(f) {
    if(!fs.existsSync(f)) return;

    var v = fs.statSync(f);
    if(!v.isDirectory()) {
        tryRm(f);
        return;
    }

    var files = fs.readdirSync(f, { withFileTypes: true });
    files.forEach(x=> {
        var name = path.join(f, x.name);
        if(x.isFile()) {
            tryRm(name);
        } else if(x.isDirectory()) {
            clearDirectory(name);
        }
    });

    tryRmdir(f);
}

function tryRmdir(f) {
    try {
        fs.rmdirSync(f);
    } catch(e) {
        console.warn(e);
    }
}

function tryRm(f) {
    try {
        fs.unlinkSync(f);
    } catch(e) {
        console.warn(e);
    }
}