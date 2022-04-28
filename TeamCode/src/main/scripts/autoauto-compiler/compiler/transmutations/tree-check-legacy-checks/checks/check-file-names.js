var fs = require("fs");
var path = require("path");

var checkSubfolders = ["T"];

var checkFiles = checkSubfolders.map(x=> {
        var folder = path.join(__dirname, "check-sources", x);
        return fs.readdirSync(folder).map(x=>path.join(folder, x));
    })
    .flat();

module.exports = checkFiles;