var fs = require("fs");
var crypto = require("crypto");
var path = require("path");

module.exports = getDirectorySha;

function getDirectorySha(directory, ignores) {
    ignores = ignores || [];
    
    directory = directory + "";
    if(!fs.existsSync(directory)) return "tree -1\u0000";
    
    var dir = fs.readdirSync(directory).sort();
    
    var fileHashBuffers = [Buffer.from("tree " + dir.length + "\u0000")];
    
    for(var i = 0; i < dir.length; i++) { 
        if(ignores.includes(dir[i])) continue;
        
        var fileSha = getFileSha(path.join(directory, dir[i]), ignores);
        fileHashBuffers.push(Buffer.from(dir[i] + "\u0000" + fileSha));
    }
    var directoryBuffer = Buffer.concat(fileHashBuffers);
    var hash = crypto.createHash("sha256").update(directoryBuffer).digest("hex");
    
    return hash;
}

function getFileSha(file, ignores) {
    file = file + "";
    if(!fs.existsSync(file)) return "blob -1\u0000";
    if(fs.statSync(file).isDirectory()) return getDirectorySha(file, ignores);
    
    var fileContent = fs.readFileSync(file);
    var gitLikeFileContentBlob = Buffer.concat([Buffer.from("blob " + fileContent.length), new Uint8Array([0]), fileContent]);
    var hash = crypto.createHash("sha256").update(gitLikeFileContentBlob).digest("hex");
    
    return hash;
}