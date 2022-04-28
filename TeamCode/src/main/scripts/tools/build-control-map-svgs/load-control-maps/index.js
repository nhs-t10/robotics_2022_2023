var fs = require("fs");
var path = require("path")

var readFileParseMap = require("./read-file-and-parse");

var teleopOpmodeFolder = path.join(__dirname, "../../../../java/org/firstinspires/ftc/teamcode/opmodes/teleop");

module.exports = function() {
    var opmodeFiles = loadJavaFilesFromFolder(teleopOpmodeFolder);

    var entries =
        opmodeFiles
        .filter(x=>matchesArgv(x))
        .map(x=>[path.basename(x, ".java"), readFileParseMap(x)])
        .filter(x=>x[1]!=undefined);
    
    //de-duplicate control maps
    var uniqueEntries = {};
    entries.forEach(x=> {
        var mapStr = JSON.stringify(x[1]);
        if(uniqueEntries[mapStr]) uniqueEntries[mapStr].push(x[0]);
        else uniqueEntries[mapStr] = [x[0]];
    });

    var resultObject = {};
    Object.keys(uniqueEntries).forEach(key=>{
        resultObject[uniqueEntries[key].join(",")] = JSON.parse(key);
    });

    return resultObject;
}

function matchesArgv(name) {
    if(process.argv.length > 2) {
        var names = process.argv.slice(2);
        var matchedName = names.find(x=>name.toLowerCase().includes(x.toLowerCase()));

        if(matchedName) return true;
        else return false;
    }
    return true;
}

function loadJavaFilesFromFolder(folder) {
    let results = [];

    let folderContents = fs.readdirSync(folder, {
        withFileTypes: true
    });

    for(var i = 0; i < folderContents.length; i++) {
        let subfile = folderContents[i];

        if(subfile.isDirectory()) {
            results = results.concat(loadJavaFilesFromFolder(path.resolve(folder, subfile.name)));
        } else if(subfile.isFile() && subfile.name.endsWith(".java")) {
            results.push(path.resolve(folder, subfile.name));
        }
    }

    return results;
}