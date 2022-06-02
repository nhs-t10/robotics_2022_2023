"use strict";

const version = require("./version");
const magic = require("./magic");

const { t_array, t_boolean, t_null, t_number, t_object, t_string, t_undefined, t_wellKnownObject } = require("./types");
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
    if (valuePool.invertedPoolMap.has(obj)) return valuePool.invertedPoolMap.get(obj);
    
    const type = getStructureType(obj) | 0;
    const entryId = (valuePool.pool.length++) | 0;
    valuePool.invertedPoolMap.set(obj, entryId);

    const vBytes = getValueBytes(type, obj, valuePool);
    vBytes.writeUInt8(type, 0);
    
    if(vBytes.length - VALUE_ENTRY_HEADER_SIZE >= 0xFFFFFFFF) throw "Serialization of value results in a chunk over 0xFFFFFFFF bytes long";
    vBytes.writeUInt32LE(vBytes.length - VALUE_ENTRY_HEADER_SIZE, 1);
    
    valuePool.pool[entryId] = vBytes;

    return entryId;
}

function getValueBytes(type, obj, valuePool) {
    switch (type) {
        case t_boolean: return getBooleanBuffer(obj);
        case t_number: return getNumberBuffer(obj);
        case t_string: return getStringBuffer(obj);
        case t_array: return getArrayElementBytes(obj, valuePool);
        case t_object: return getEntriesBytes(obj, valuePool, VALUE_ENTRY_HEADER_SIZE);
        case t_wellKnownObject:
            //wellknownobjects record their constructor so they can be re-constructed later
            return getWellKnownObjectBytes(obj, valuePool); 
        case t_undefined:
        case t_null:
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
    
    strBuf.copy(strBufWithHead, VALUE_ENTRY_HEADER_SIZE);
    
    return strBufWithHead;
}

function getStructureType(obj) {
    const type = typeof obj;
    
    if(obj === null) return t_null; 
    if(Array.isArray(obj)) return t_array;
    if (type === "object" && obj.constructor != Object && wellKnownConstructors.isWellKnownObject(obj)) return t_wellKnownObject;
    
    switch(type) {
        case "boolean": return t_boolean;
        case "number": return t_number;
        case "string": return t_string;
        case "object": return t_object;
        case "undefined": return t_undefined;
        default: throw new Error("Error serialising value of type " + type);
    }
}

function getArrayElementBytes(arr, valuePool) {
    const b = Buffer.allocUnsafe(arr.length * 4 + VALUE_ENTRY_HEADER_SIZE);
    
    let i = VALUE_ENTRY_HEADER_SIZE;
    //This `Array.isArray` check will always evaluate to true. It's still here because V8 can optimize the `for` loop better with that assurance.
    if(Array.isArray(arr)) {
        for(const elem of arr) {
            b.writeUInt32LE(createOrGetIdInValuepool(elem, valuePool), i);
            i += 4;
        }
    }
    
    return b;
}

function getEntriesBytes(obj, valuePool, requiredHeaderLength) {
    const keys = Object.keys(obj)
    const b = Buffer.allocUnsafe(keys.length * 8 + requiredHeaderLength);
    
    let i = requiredHeaderLength;
    for(const prop of keys) {
        b.writeUInt32LE(createOrGetIdInValuepool(prop, valuePool), i);
        b.writeUInt32LE(createOrGetIdInValuepool(obj[prop], valuePool), i + 4);
        i += 8;
    }

    return b;
}

function getWellKnownObjectBytes(obj, valuePool) {
    const keyvalues = getEntriesBytes(obj, valuePool, 8 + VALUE_ENTRY_HEADER_SIZE);
    
    //write header info-- namely, the constructor name and an argument.
    writeWellKnownInfo(obj, valuePool, keyvalues, VALUE_ENTRY_HEADER_SIZE);
    
    return keyvalues;
}

function writeWellKnownInfo(obj, valuePool, resultInfoBuffer, offset) {

    var constructorName = wellKnownConstructors.getConstructorName(obj);
    var constructorPoolId = createOrGetIdInValuepool(constructorName, valuePool);
    
    var paramVal = undefined;
    if(typeof obj.valueOf === "function" && !obj.hasOwnProperty("valueOf")) {
        paramVal = obj.valueOf();
        //if valueOf is recursive, then don't use it
        if(paramVal == obj) paramVal = undefined;
    }
    
    resultInfoBuffer.writeUInt32LE(constructorPoolId, offset);
    resultInfoBuffer.writeUInt32LE(createOrGetIdInValuepool(paramVal, valuePool), offset + 4);
}