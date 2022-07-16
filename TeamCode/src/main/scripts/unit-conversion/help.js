
const unitConversion = require("./index");

const ALLOWED_DIMENSIONS = ["L", "A", "T"];

module.exports = {
    printUnitsAndExit: printUnitsAndExit,
    getUnitsString: getUnitsString
}

function printUnitsAndExit() {
    printUnits();
    process.exit();
}

function printUnits() {
    const unitData = unitConversion.getUnitData().data;

    for(const key in unitData) {
        const unit = unitData[key];

        if(ALLOWED_DIMENSIONS.includes(unit.dimension)) {
            console.log("- " + unit.key);
            console.log("  Abbreviations: " + unit.abbs.join(", "));
        }
    }
}

function getUnitsString(dimensionBases) {
    const unitData = unitConversion.getUnitData().data;

    let resultString = "";

    for(const key in unitData) {
        const unit = unitData[key];
        resultString += `-   ${unit.key}\n    Abbreviations: ${unit.abbs.join(", ")}\n`;
    }

    return resultString;
}