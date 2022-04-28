var bitwiseyHelpers = require("./bitwisey-helpers.js");

var crcTable = (function(){
    var c;
    var crcTable = [];
    for(var n =0; n < 256; n++){
        c = n;
        for(var k =0; k < 8; k++){
            c = ((c&1) ? (0xEDB88320 ^ (c >>> 1)) : (c >>> 1));
        }
        crcTable[n] = c;
    }
    return crcTable;
})();

module.exports = function(str) {
    var data = Buffer.from(str, "utf8");

    var crc32 = 0 ^ (-1);
    for(var i = 0; i < data.length; i++) {
        var byte = data[i];
        crc32 = (crc32 >>> 8) ^ crcTable[(crc32 ^ byte) & 0xFF];
    }

    var final = (crc32 ^ (-1)) >>> 0;

    return bitwiseyHelpers.numToB62(final);
}