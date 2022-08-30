"use strict";

const fs = require("fs");
const crypto = require("crypto");
const folderScanner = require("../../../../folder-scanner");

module.exports = getDirectorySha;

async function getDirectorySha(directory, ignores) {
    ignores = ignores || [];
    
    const scanner = folderScanner(directory, x=>!ignores.includes(x), true);
    
    const hash = crypto.createHash("sha256");
    for await (const fileAddress of scanner) {
        const fileHash = await getFileSha(fileAddress);
        hash.update(fileHash);
    }
    
    return hash.digest("hex");
}

async function getFileSha(fileAddress) {
    return new Promise(function(resolve, reject) {
        if (!fs.existsSync(fileAddress)) resolve("blob -1\u0000");
        
        fs.readFile(fileAddress, function(err, fileContent) {
            if(err) reject(err);
            
            var gitLikeFileContentBlob = Buffer.concat([Buffer.from(fileAddress + "\u0000"), fileContent]);
            var hash = crypto.createHash("sha256").update(gitLikeFileContentBlob).digest("hex");

            resolve(hash);
        });
    })
}