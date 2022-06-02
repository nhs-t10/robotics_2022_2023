"use strict";

const magic = require("./magic");
const version = require("./version");
const arrayReader = require("../../script-helpers/array-reader");
const types = require("./types");
const wellKnownConstructors = require("./well-known-constructors");

module.exports = bufferToObject


function bufferToObject(buf, valueOnUnreadable) {
    //reader starts at `i=magic.length` to skip over the magic values.
    const reader = arrayReader(buf, magic.length);

    if (reader.read() != version) return valueOnUnreadable;

    let pool = {};

    reconstructPool(pool, reader);

    return getHydratedByIndex(0, pool);
}

function readUndefined(entry) {
    entry.value = undefined;
    entry.hydrated = true;
}
function readBoolean(entry, reader) {
    entry.value = !!reader.read();
    entry.hydrated = true;
}
function readNumber(entry, reader) {
    entry.value = reader.readNextBytes(entry.contentLength).readDoubleLE(0);
    entry.hydrated = true;
}
function readString(entry, reader) {
    const idx = reader.currentIndex();
    entry.value = reader.buffer().toString("utf8", idx, idx + entry.contentLength);
    entry.hydrated = true;
    
    reader.skip(entry.contentLength);
}
function readNull(entry) {
    entry.value = null;
    entry.hydrated = true;
}
function readObject(entry, reader) {
    entry.keyValues = new Array(entry.contentLength / 8);
    const entryCount = entry.keyValues.length;

    for (let i = 0; i < entryCount; i++) {
        entry.keyValues[i] = ([
            reader.readUInt32LE(),
            reader.readUInt32LE()
        ]);
    }
}
function readWellKnownObject(entry, reader) {
    entry.constructorName = reader.readUInt32LE();

    const constrArgId = reader.readUInt32LE();
    if (constrArgId && constrArgId != entry.id) {
        entry.constructorArg = constrArgId;
    }

    readObject(entry, reader);
}

function readArray(entry, reader) {
    entry.values = new Array(entry.contentLength / 4);
    
    const entryCount = entry.values.length;
    for (let i = 0; i < entryCount; i++) {
        entry.values[i] = reader.readUInt32LE();
    }
}

function getHydratedByIndex(index, pool) {
    return getHydratedValue(pool[index], pool);
}

function getHydratedValue(entry, pool) {
    if (entry.hydrated) return entry.value;

    switch (entry.typeId) {
        case types.t_array: return getHydratedArray(entry, pool);
        case types.t_object: return getHydratedObject(entry, pool);
        case types.t_wellKnownObject: return getHydratedWellKnownObject(entry, pool);
        default: throw new Error("Attempt to hydrate unknown or unsuited type " + entry.typeId);
    }
}

function getHydratedObject(entry, pool) {
    if (!entry.hydrated) entry.value = {};
    entry.hydrated = true;

    for (const kv of entry.keyValues) {
        entry.value[getHydratedByIndex(kv[0], pool)] = getHydratedByIndex(kv[1], pool);
    }

    return entry.value;
}

function getHydratedArray(entry, pool) {
    if (!entry.hydrated) {
        entry.value = new Array(entry.values.length);
        entry.hydrated = true;
        
        var i = 0;
        for(const v of entry.values) {
            entry.value[i] = getHydratedByIndex(v, pool);
            i++;
        }
    }

    return entry.value;
}

function getHydratedWellKnownObject(entry, pool) {
    if (!entry.hydrated) {
        const constrName = getHydratedByIndex(entry.constructorName, pool);
        const constr = wellKnownConstructors.byName(constrName);

        try {
            if ("constructorArg" in entry) entry.value = new constr(getHydratedByIndex(entry.constructorArg, pool));
            else entry.value = new constr();
        } catch (e) {
            entry.value = Object.create(constr.prototype);
        }
    }
    entry.hydrated = true;

    return getHydratedObject(entry, pool);
}

function readPoolEntry(index, reader, pool) {
    const entry = readPoolHeader(index, reader, pool);

    switch (entry.typeId) {
        case types.t_undefined: readUndefined(entry); break;
        case types.t_null: readNull(entry); break;

        case types.t_boolean: readBoolean(entry, reader); break;
        case types.t_number: readNumber(entry, reader); break;
        case types.t_string: readString(entry, reader); break;

        case types.t_array: readArray(entry, reader); break;
        case types.t_object: readObject(entry, reader); break;

        case types.t_wellKnownObject: readWellKnownObject(entry, reader); break;

        default: throw new Error("Unknown structured-serialize'd type '" + entry.typeId + "'");
    }
}

function readPoolHeader(index, reader, pool) {
    const typeCode = reader.read();
    const length = reader.readUInt32LE();

    return (pool[index] = ({
        typeId: typeCode,
        contentLength: length,
        id: index,
        hydrated: false,
        value: null,
        keyValues: [],
        values: [],
        constructorName: ""
    }));
}

function reconstructPool(pool, reader) {
    let index = 0;

    while (reader.hasNext()) {
        readPoolEntry(index, reader, pool);
        index++;
    }
}
