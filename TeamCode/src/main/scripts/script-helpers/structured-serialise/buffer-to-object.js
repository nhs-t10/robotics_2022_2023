var magic = require("./magic");
var version = require("./version");
var arrayReader = require("../../script-helpers/array-reader");
var bitwiseyHelpers = require("../bitwisey-helpers");
var types = require("./types");
const wellKnownConstructors = require("./well-known-constructors");

module.exports = bufferToObject


function bufferToObject(buf) {
    //reader starts at `i=magic.length` to skip over the magic values.
    const reader = arrayReader(buf, magic.length);
    
    if(reader.read() != version) throw new Error("Version mismatch in structured-serialise!");
    
    let pool = [];
    
    reconstructPool(pool, reader);
    
    return getHydratedValue(pool[0], pool);
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
    entry.value = bitwiseyHelpers.numberFromBytes(reader.readNextBytes(entry.contentLength));
    entry.hydrated = true;
}
function readString(entry, reader) {
    entry.value = reader.readNextBytes(entry.contentLength).toString("utf8");
    entry.hydrated = true;
}
function readNull(entry) {
    entry.value = null;
    entry.hydrated = true;
}
function readObject(entry, reader, pool) {
    entry.keyValues = [];

    while(reader.hasNext()) {
        var k = getIntoPool(pool, reader.readVarint());
        
        var v = getIntoPool(pool, reader.readVarint());

        entry.keyValues.push([k,v]);
    }
}
function readWellKnownObject(entry, reader, pool) {    
    entry.constructorName = getIntoPool(pool, reader.readVarint());
    
    var constrArgId = reader.readVarint();
    if(constrArgId && constrArgId != entry.id) {
        entry.constructorArg = getIntoPool(pool, constrArgId);
    }

    readObject(entry, reader, pool);
}

function getHydratedValue(entry, pool) {
    if(entry.hydrated) return entry.value;

    switch(entry.typeId) {
        case types.array: return getHydratedArray(entry, pool);
        case types.object: return getHydratedObject(entry, pool);
        case types.wellKnownObject: return getHydratedWellKnownObject(entry, pool);
        default: throw new Error("Attempt to hydrate unknown or unsuited type " + entry.typeId);
    }
}

function getHydratedObject(entry, pool) {
    if(!entry.hydrated) entry.value = {};
    entry.hydrated = true;

    for(const kv of entry.keyValues) {
        var kH = getHydratedValue(kv[0], pool);
        var vH = getHydratedValue(kv[1], pool);

        entry.value[kH] = vH;
    }

    return entry.value;
}

function getHydratedArray(entry, pool) {
    if(!entry.hydrated) {
        entry.value = [];
        entry.hydrated = true;
    }

    return getHydratedObject(entry, pool);
}

function getHydratedWellKnownObject(entry, pool) {
    if(!entry.hydrated) {
        var constrName = getHydratedValue(entry.constructorName, pool);
        var constr = wellKnownConstructors.byName(constrName);

        try {
            if("constructorArg" in entry) entry.value = new constr(getHydratedValue(entry.constructorArg, pool));
            else entry.value = new constr();
        } catch(e) {
            entry.value = Object.create(constr.prototype);
        }
    }
    entry.hydrated = true;

    return getHydratedObject(entry, pool);
}

function readPoolEntry(entry, reader, pool) {
    reader.setBound(entry.contentLength);
    switch(entry.typeId) {
        case types.undefined: readUndefined(entry); break;
        case types.null: readNull(entry); break;

        case types.boolean: readBoolean(entry, reader); break;
        case types.number: readNumber(entry, reader); break;
        case types.string: readString(entry, reader); break;

        case types.array: 
        case types.object: readObject(entry, reader, pool); break;

        case types.wellKnownObject: readWellKnownObject(entry, reader, pool); break;

        default: throw new Error("Unknown structured-serialize'd type '" + entry.typeId + "'");
    }
    reader.skipToBound();
    reader.releaseBound();
}

function reconstructPool(pool, reader) {
    var index = 0;
    
    while(reader.hasNext()) {
        var typeCode = reader.read();
        var length = reader.readVarint();
        
        var entry = upsertIntoPool(pool, index, { typeId: typeCode, contentLength: length, id: index, hydrated: false, value: null, keyValues: [], constructorName: "" });

        readPoolEntry(entry, reader, pool);

        index++;
    }
}

function upsertIntoPool(pool, id, entry) {
    return Object.assign(getIntoPool(pool, id), entry);
}

function getIntoPool(pool, id) {
    if(pool[id]) return pool[id];
    else return pool[id] = {};
}