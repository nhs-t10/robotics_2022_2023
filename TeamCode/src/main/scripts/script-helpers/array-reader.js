//Like Java's stream classes, this array-reader keeps a reference of where it is, and allows the user to read 1 item at once.

/**
 * 
 * @param {Buffer} buffer 
 * @param {number?} i 
 * @returns Reader
 */
module.exports = function(buffer, i) {
    var index = i|0;
    var len = buffer.length;
    return {
        currentIndex: function() {
            return index;
        },
        buffer: function() {
            return buffer;
        },
        skip: function(l) {
            index += (l|0);
        },
        read: function() {
            return buffer[index++];
        },
        readUInt32LE: function() {
            index += 4;
            return buffer.readUInt32LE(index - 4);
        },
        readVarint: function() {
            var result = 0;
            for(; index < len; index++) {
                result |= (buffer[index] >>> 1);

                if(buffer[index] & 0b1) break;
                else result <<= 7;
            }

            //move the pointer off of the last item
            index++;
            return result;
        },
        readNextBytes: function(l) {
            l|=0;
            index += l;
            return buffer.slice(index - l, index);
        },
        hasNext: function() {
            return index < len;
        },
        setBound: function(nextByteCount) {
            len = index + nextByteCount;
        },
        releaseBound: function() {
            len = buffer.length;
        },
        skipToBound: function() {
            index = len;
        }
    }
}