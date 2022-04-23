var version = require("./version");
var magic = require("./magic");

var typeCodes = require("./types");
var bitwiseyTools = require("../../script-helpers/bitwisey-helpers");
var wellKnownConstructors = require("./well-known-constructors");

module.exports = function(obj) {
    var valuePool = {
        pool: [],
        invertedPoolMap: new Map()
    };

    createOrGetIdInValuepool(obj, valuePool);

    return poolToBuffer(valuePool.pool);
}

function poolToBuffer(pool) {
    return packageIntoBuffer([].concat(...pool.map(x=>x.bytes)));
}

function packageIntoBuffer(buffer) {
    return Buffer.from(
        [].concat(magic, [version], buffer)
    );
}

function createOrGetIdInValuepool(obj, valuePool) {
    var type = (obj === null ? "null" : typeof obj) + "";
    var cstr;
    if(type === "object") {
        cstr = wellKnownConstructors.getName(obj);
        if(cstr) type = "wellKnownObject";
    }
    
    if(typeCodes[type] === undefined) {
        console.error(obj);
        throw new Error("Could not serialise value of type " + type);
    }

    //by searching from the back, we get more-recent values first
    if(valuePool.invertedPoolMap.has(obj)) return valuePool.invertedPoolMap.get(obj);

    var poolEntry = {value: obj, id: valuePool.pool.length++, bytes: []};
    valuePool.pool[poolEntry.id] = poolEntry;
    valuePool.invertedPoolMap.set(obj, poolEntry.id);

    switch(type) {
        case "undefined": poolEntry.bytes = [];
        break;
        case "boolean": poolEntry.bytes = [+obj];
        break;
        case "number": poolEntry.bytes = bitwiseyTools.numberToVarBytes(obj);
        break;
        case "string": poolEntry.bytes = Array.from(Buffer.from(obj, "utf8"));
        break;
        case "object": poolEntry.bytes = getEntriesBytes(obj, valuePool);
        break;
        case "null": poolEntry.bytes = [];
        break;
        case "wellKnownObject":
            //wellknownobjects record their constructor so they can be re-constructed later
            poolEntry.bytes = getWellKnownInfo(cstr, obj, valuePool).concat(getEntriesBytes(obj, valuePool));
    }

    poolEntry.bytes = [typeCodes[type]].concat(bitwiseyTools.toVarintBytes(poolEntry.bytes.length), poolEntry.bytes);

    return poolEntry.id;
}

function getEntriesBytes(obj, valuePool) {

    var propNames = Object.getOwnPropertyNames(obj);
    var b = [];
    
    for(const prop of propNames) {
        var kB = bitwiseyTools.toVarintBytes(createOrGetIdInValuepool(prop, valuePool));
        var vB = bitwiseyTools.toVarintBytes(createOrGetIdInValuepool(obj[prop], valuePool));

        b.push(kB, vB);
    }

    return b.flat();
}

function getWellKnownInfo(constructorName, obj, valuePool) {
    var constructorPoolId = createOrGetIdInValuepool(constructorName, valuePool);
    var constructorPoolBytes = bitwiseyTools.toVarintBytes(constructorPoolId);
    
    var paramVal = undefined;
    if(typeof obj.valueOf === "function" && !obj.hasOwnProperty("valueOf")) {
        paramVal = obj.valueOf();
        //if valueOf is recursive, then don't use it
        if(paramVal == obj) paramVal = undefined;
    }

    var paramPoolBytes = bitwiseyTools.toVarintBytes(createOrGetIdInValuepool(paramVal, valuePool));
    
    return constructorPoolBytes.concat(paramPoolBytes);
}