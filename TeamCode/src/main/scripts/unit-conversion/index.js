var UCONV_VERSION = 1;

var updateData = require("./update");
var cache = require("../cache");

var unitData = loadData();

module.exports = {
    getUnitForAbbreviation: getUnitForAbbreviation,
    getUnitForKey: getUnitForKey,
    dimensions: require("./dimensions")
}

function getUnitForKey(k) {
    return unitData.data[k];
}

function getUnitForAbbreviation(abb) {
    var key = unitData.index[abb];
    
    if(!key) return undefined;
    else if(key.length > 1) return discriminateToUnique(key);
    else return unitData.data[key[0]];
}

function discriminateToUnique(unitNames) {
    var uniq = [];
    var used = {}
    
    for(var i = 0; i < unitNames.length; i++) {
        var record = unitData.data[unitNames[i]];
        if(!used[record.conversionFactors]) {
            uniq.push(unitNames[i]);
            used[record.conversionFactors] = true;
        }
    }
    if(uniq.length == 1) return unitData.data[uniq[0]];
    else return uniq;
}

function loadData() {
    var data = {v: NaN, index: {}, data: {}}; 
    try {    data = require("./units.json");    } catch(e) {}

    if(data.v != UCONV_VERSION) updateData(UCONV_VERSION);

    return data;
}