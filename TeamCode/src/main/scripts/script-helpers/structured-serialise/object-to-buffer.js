"use strict";

const version = require("./version");
const magic = require("./magic");

const typeCodes = require("./types");
const wellKnownConstructors = require("./well-known-constructors");

const VALUE_ENTRY_HEADER_SIZE = 5;

module.exports = objectToBuffer;

function objectToBuffer(obj) {
    const valuePool = {
        pool: [],
        invertedPoolMap: new Map()
    };

    createOrGetIdInValuepool(obj, valuePool);

    return poolToBuffer(valuePool.pool);
}

function poolToBuffer(pool) {
    return addHeaderToBuffer(Buffer.concat(pool));
}

//WARNING: USES UNSAFE MEMORY THINGS.
//WORKS, BUT DON'T MESS WITH!
//here is a more understandable version:
/*
    return Buffer.from(
        [].concat(magic, [version], originBlob)
    );
*/
/**
 * 
 * @param {Buffer} originBlob 
 * @returns Buffer
 */
function addHeaderToBuffer(originBlob) {
    const magicLen = magic.length;
    const b = Buffer.allocUnsafe(magicLen + 1 + originBlob.length);

    for(let i = 0; i < magicLen; i++) b[i] = magic[i];
    b[magicLen] = version;
    originBlob.copy(b, magicLen + 1, 0, originBlob.length);
    
    return b;
}

function createOrGetIdInValuepool(obj, valuePool) {

    const type = getStructureType(obj);

    if(valuePool.invertedPoolMap.has(obj)) return valuePool.invertedPoolMap.get(obj);

    const entryId = valuePool.pool.length++
    valuePool.invertedPoolMap.set(obj, entryId);

    const vBytes = getValueBytes(type, obj, valuePool);
    vBytes.writeUInt8(type, 0);
    vBytes.writeUInt32LE(vBytes.length - VALUE_ENTRY_HEADER_SIZE, 1);
    
    valuePool.pool[entryId] = vBytes;

    return entryId;
}

function getValueBytes(type, obj, valuePool) {
    switch (type) {
        case typeCodes.boolean: return getBooleanBuffer(obj);
        case typeCodes.number: return getNumberBuffer(obj);
        case typeCodes.string: return getStringBuffer(obj);
        case typeCodes.array: return getArrayElementBytes(obj, valuePool);
        case typeCodes.object: return getEntriesBytes(obj, valuePool, 0);
        case typeCodes.wellKnownObject:
            //wellknownobjects record their constructor so they can be re-constructed later
            return getWellKnownObjectBytes(obj, valuePool); 
        case typeCodes.undefined:
        case typeCodes.null:
        default: 
            //null and undefined will fall down here
            return Buffer.allocUnsafe(VALUE_ENTRY_HEADER_SIZE);
    }
    
}

function getBooleanBuffer(bool) {
    const boolBuf = Buffer.allocUnsafe(1 + VALUE_ENTRY_HEADER_SIZE);
    boolBuf.writeUInt8(+!!bool, VALUE_ENTRY_HEADER_SIZE);
    return boolBuf;
}

function getNumberBuffer(num) {
    const numBuf = Buffer.allocUnsafe(8 + VALUE_ENTRY_HEADER_SIZE);
    numBuf.writeDoubleLE(num, VALUE_ENTRY_HEADER_SIZE);
    return numBuf;
}

function getStringBuffer(str) {
    const strBuf = Buffer.from(str, "utf8");
    const strBufWithHead = Buffer.allocUnsafe(strBuf.length + VALUE_ENTRY_HEADER_SIZE);
    
    strBufWithHead.fill(str, VALUE_ENTRY_HEADER_SIZE, strBufWithHead.length, "utf8");
    strBuf.copy(strBufWithHead, VALUE_ENTRY_HEADER_SIZE);
    
    return strBufWithHead;
}

function getStructureType(obj) {
    const type = typeof obj;
    
    if(obj === null) {
        return typeCodes.null;
    } else if(Array.isArray(obj)) {
        return typeCodes.array;
    } else if (type === "object" && wellKnownConstructors.isWellKnownObject(obj)) {
        return typeCodes.wellKnownObject;
    } else {
        return typeCodes[type];
    }
}

function getArrayElementBytes(arr, valuePool) {
    const b = Buffer.allocUnsafe(arr.length * 4 + VALUE_ENTRY_HEADER_SIZE);
    
    let i = VALUE_ENTRY_HEADER_SIZE;
    for(const elem of arr) {
        b.writeUInt32LE(createOrGetIdInValuepool(elem, valuePool), i);
        i += 4;
    }
    
    return b;
}

function getEntriesBytes(obj, valuePool, requiredHeaderLength) {
    const keys = Object.keys(obj)
    const b = Buffer.allocUnsafe(keys.length * 8 + requiredHeaderLength + VALUE_ENTRY_HEADER_SIZE);
    
    let i = requiredHeaderLength + VALUE_ENTRY_HEADER_SIZE;
    for(const prop of keys) {
        b.writeUInt32LE(createOrGetIdInValuepool(prop, valuePool), i);
        b.writeUInt32LE(createOrGetIdInValuepool(obj[prop], valuePool), i + 4);
        i += 8;
    }

    return b;
}

function getWellKnownObjectBytes(obj, valuePool) {
    const keyvalues = getEntriesBytes(obj, valuePool, 8);
    
    //write header info-- namely, the constructor name and an argument.
    writeWellKnownInfo(obj, valuePool, keyvalues);
    
    return keyvalues;
}

function writeWellKnownInfo(obj, valuePool, resultInfoBuffer) {

    var constructorName = wellKnownConstructors.getName(obj);
    var constructorPoolId = createOrGetIdInValuepool(constructorName, valuePool);
    
    var paramVal = undefined;
    if(typeof obj.valueOf === "function" && !obj.hasOwnProperty("valueOf")) {
        paramVal = obj.valueOf();
        //if valueOf is recursive, then don't use it
        if(paramVal == obj) paramVal = undefined;
    }
    
    resultInfoBuffer.writeUInt32LE(constructorPoolId, 0);
    resultInfoBuffer.writeUInt32LE(createOrGetIdInValuepool(paramVal, valuePool), 4);
}