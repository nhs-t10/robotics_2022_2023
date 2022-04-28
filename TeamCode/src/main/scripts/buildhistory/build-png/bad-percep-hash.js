var crc = require("./png-file/crc");

module.exports = {
    hash: hashBuffer,
    combineHashes: combineHashes
}

/**
 * 
 * @param {Buffer[]} hashes 
 * @returns Buffer
 */
function combineHashes(hashes) {
    hashes = hashes.filter(x=>x!=null);

    if(hashes.length == 0) return hashBuffer(Buffer.from([]));

    var avg = [];
    for(var i = 0; i < hashes[0].length - 1; i+=2) {
        var cellTotal = 0;
        for(var j = 0; j < hashes.length; j++) {
            cellTotal += (hashes[j][i] << 8) | hashes[j][i + 1];
        }

        if(cellTotal > 0xff_ff) cellTotal %= 0xff_ff;

        avg.push(cellTotal);
    }

    return bufferFrom16BitNums(avg);
}

/**
 * Compute a (bad) perceptual hash.
 * @param {Buffer} buffer Data to consume.
 * @returns {Buffer} Hash of the data.
 */
function hashBuffer(buffer) {
    var buckets = [];
    
    //fill all the buckets with zeros
    for(var i = 0; i < 256; i++) buckets.push(0);
    
    var dataCrc = crc(buffer);
    var dataCrcLastByte = dataCrc[dataCrc.length - 1];
    
    var lenCode = (dataCrc[1] << 8) | dataCrc[2];
    
    if(lenCode >= 0xff_ff) lenCode %= 0xff_ff;
    
    if(buckets[dataCrcLastByte] !== undefined) buckets[dataCrcLastByte] = lenCode;
    
   return bufferFrom16BitNums(buckets);
}

function bufferFrom16BitNums(buckets) {
    var byteBuckets = [];
    
     //turn two-byte numbers into two one-bit numbers
     for(var i = 0; i < buckets.length; i ++) {
        var bucketVal = buckets[i];
        if(bucketVal > 0xff_ff) bucketVal %= 0xff_ff;

        byteBuckets.push(bucketVal >> 8);
        byteBuckets.push(bucketVal & 0b1111_1111);
     }
     
     
     return Buffer.from(byteBuckets);
}