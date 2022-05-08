const fs = require("fs");
const crypto = require("crypto");
const folderScanner = require("../../../../folder-scanner");

module.exports = getDirectorySha;

async function getDirectorySha(directory, ignores) {
    ignores = ignores || [];
    
    directory = directory + "";
    if(!fs.existsSync(directory)) return "";
    
    const scanner = folderScanner(directory, x=>!ignores.includes(x), true);
    
    const hash = crypto.createHash("sha256");
    for await (const fileAddress of scanner) {
        hash.update(getFileSha(fileAddress));
    }
    
    return hash.digest("hex");
}

function getFileSha(fileAddress) {
    if (!fs.existsSync(fileAddress)) return "blob -1\u0000";
    
    var fileContent = fs.readFileSync(fileAddress);
    var gitLikeFileContentBlob = Buffer.concat([Buffer.from(fileAddress + "\u0000"), fileContent]);
    var hash = crypto.createHash("sha256").update(gitLikeFileContentBlob).digest("hex");
    
    return hash;
}