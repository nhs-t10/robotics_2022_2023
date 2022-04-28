module.exports = {
/**
* Encode integers extremely efficiently. Every 7 bits, if the integer is over, insert a 1 bit. Otherwise, insert a 0.
* Encoding an integer is very efficient to process, because it never has to use string conversions or anything. It's all bitwise operators!
* Simple, easy-- that's the power of the home depot :)
* https://en.wikipedia.org/wiki/Variable-length_code
*/
toVarint8: function toVarint8(num) {
    var result = 0;

    var sectionsScanned = 0;
    while(num > 0) {
        var currentSection = 0b1111_111 & num;
        num >>>= 7;

        var isLastChunk = sectionsScanned == 0;
        var encodedChunk = (currentSection << 1) | (isLastChunk ? 1 : 0);
        result |= (encodedChunk << (sectionsScanned * 8));
        sectionsScanned++;
    }
    return result;
},

toVarintBytes: function toVarintBytes(num) {
    var result = [];
    
    if(num == 0) return [1];

    var sectionsScanned = 0;
    while(num > 0) {
        var currentSection = 0b1111_111 & num;
        num >>>= 7;

        var isLastChunk = sectionsScanned == 0;
        var encodedChunk = (currentSection << 1) | (isLastChunk ? 1 : 0);
        result.unshift(encodedChunk);
        sectionsScanned++;
    }
    return result;
},

numberToBytes: function(num) {
    var ab = new ArrayBuffer(8);
    var buf = new DataView(ab);
    buf.setFloat64(0, num);

    var b = [];
    for(var i = 0; i < 8; i++) b.push(buf.getUint8(i));
    return b;
},

numberToVarBytes: function(num) {
    var b = this.numberToBytes(num);
    while(b[b.length - 1] == 0) b.pop();
    return b;
},
numberFromBytes: function(bytes) {
    var ab = new ArrayBuffer(8);
    bytes.copy(new Uint8Array(ab, 0, 8));
    var buf = new DataView(ab);

    return buf.getFloat64(0);
},

numToB62: function numToB62(i) {
    i = Math.round(i);
    var r = "";
    if(r < 0) {
        r += "-";
        i *= -1;
    }

    while(i > 0) {
        var m = i % 62;
        if(m < 36) r += m.toString(36);
        else if(m < 61) r += (m - 26).toString(36).toUpperCase();
        else r += "_"

        i /= 62;
        i = Math.floor(i);
    }
    return r || "0";
}
}