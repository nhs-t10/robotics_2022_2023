"use strict";

const UCONV_VERSION = 1;

const updateData = require("./update");
const path = require("path");
const { readJSONFile } = require("../script-helpers/safe-fs-utils");

const unitData = loadData();

module.exports = {
    getUnitForAbbreviation: getUnitForAbbreviation,
    getUnitForKey: getUnitForKey,
    dimensions: require("./dimensions"),
    getUnitData: ()=>unitData
}

/**
 * 
 * @param {string} k 
 * @returns {UnitRecord | undefined}
 */
function getUnitForKey(k) {
    return unitData.data[k];
}

/**
 * 
 * @param {string} abb 
 * @returns {UnitRecord | UnitRecord[] | undefined}
 */
function getUnitForAbbreviation(abb) {
    if (abb in unitData.index) {
        const potentialDataKeys = unitData.index[abb];
        return discriminateToUnique(potentialDataKeys);
    } else if(abb.endsWith("s")) {
        //try removing the plural 's' from end
        return getUnitForAbbreviation(abb.slice(0, -1));
    } else {
        return undefined;
    }
}

/**
 * 
 * @param {string[]} unitNames 
 * @returns {UnitRecord | UnitRecord[]}
 */
function discriminateToUnique(unitNames) {
    var uniq = [];
    var used = {}

    for (var i = 0; i < unitNames.length; i++) {
        var record = unitData.data[unitNames[i]];
        if (!used[record.conversionFactors]) {
            uniq.push(unitNames[i]);
            used[record.conversionFactors] = true;
        }
    }
    if (uniq.length == 1) return unitData.data[uniq[0]];
    else return uniq;
}

/**
 * 
 * @returns {VersionedUnitData}
 */
function loadData() {
    const dataFile = path.join(__dirname, "units.json");
    const data = readJSONFile(dataFile, { v: NaN, index: {}, data: {} });

    if (data.v != UCONV_VERSION) updateData(UCONV_VERSION);

    return data;
}

/**
 * @typedef {object} VersionedUnitData
 * @property {number} v
 * @property {UnitData} data
 * @property {UnitIndex} index
 */

/**
 * @typedef {Object.<string, string[]>} UnitIndex
 * 
 */

/**
 * @typedef {Object.<string, UnitRecord>} UnitData
 */

/**
 * @typedef {object} UnitRecord
 * @property {string} key
 * @property {string} unit
 * @property {string} dimension
 * @property {string[]} abbs
 * @property {number} conversionFactors
 */