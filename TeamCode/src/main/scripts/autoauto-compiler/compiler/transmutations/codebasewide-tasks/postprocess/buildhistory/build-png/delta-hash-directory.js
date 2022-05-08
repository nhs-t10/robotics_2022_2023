var fs = require("fs");
const folderScanner = require("../../../../../folder-scanner");

var badPerceptualHash = require("./bad-percep-hash");

module.exports = async function(directory, oldHashString, ignores) {
    var hashBuffer = await getDirectoryPerceptualHash(directory, ignores);
    var hashString = hashBuffer.toString("hex");
    
    return {
        hash: hashString,
        diff: hexDiff(hashString, oldHashString)
    }
}

async function getDirectoryPerceptualHash(directory, ignores) {
    ignores = ignores || [];
    
    directory = directory + "";
    if(!fs.existsSync(directory)) return null;
    
    const scanner = folderScanner(directory, x=>!ignores.includes(x));
    
    var hashes = [];
    
    for await(const file of scanner) {
        hashes.push(getFilePerceptualHash(file));
    }
    
    return badPerceptualHash.combineHashes(hashes);
}

function getFilePerceptualHash(file) {
    var fileContent = fs.readFileSync(file);
    var hash = badPerceptualHash.hash(fileContent);
    
    return hash;
}


function hexDiff(a, b) {
    var res = "";
    
    if(!a) return b;
    if(!b) return a;
    
    for(var i = 0; i < a.length; i++) {
        var counterpoint = b[i] || 0;
        var counterNumber = +('0x' + counterpoint);
        
        var digit = +('0x' + a[i]);
        
        var delta = digit - counterNumber;

        if(delta != 0) {
            //convert a negative delta to an unsigned 16-bit delta
            while(delta < 0) delta += 0xff_ff;
            if(delta >= 0xff_ff) delta %= 0xff_ff;

            var hexString = (delta & 0xff).toString(16);

            res += hexString;
        }
    }
    return res;
}