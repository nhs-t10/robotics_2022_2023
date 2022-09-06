"use strict";

const { type } = require("os");
const androidStudioLogging = require("../../../../../../script-helpers/android-studio-logging");
const { formatType, formatLocation } = require("./format-types");

module.exports = getBinaryOperatorResult;

/**
 * @typedef {import("./format-types").TypeSystem} TypeSystem
 * @typedef {import("./format-types").TypeRecord} TypeRecord
 * @typedef {import("../../../text-to-syntax-tree/parser").Location} Location
 */

/**
 * @param {TypeRecord} type
 * @param {TypeRecord[]} leftType
 * @param {TypeRecord[]} rightType
 * @param {TypeSystem} typeSystem
 * @returns {TypeRecord}
 */
function getBinaryOperatorResult(type, leftType, rightType, typeSystem) {    
    const location = type.location;
    
    const leftLocation = typeSystem[type.left].location, rightLocation = typeSystem[type.right].location;
    
    leftType.location = leftLocation;
    rightType.location = rightLocation;

    switch(type.op) {
        case "-": return minusOp(leftType, rightType, location, typeSystem);
        case "+": return plusOp(leftType, rightType, location, typeSystem);
        case "*": return timesOp(leftType, rightType, location, typeSystem);
        case "/": return divideOp(leftType, rightType, location, typeSystem);
        case "%": return modOp(leftType, rightType, location, typeSystem);
        case "**":
        case "^": return expOp(leftType, rightType, location, typeSystem);

        case ">":
        case ">=":
        case "==":
        case "!=":
        case "<=":
        case "<":
            return typeSystem["boolean"] || console.warn("no boolean!");

        default: throw new Error("unknown operator " + op);
    }
}

/**
 * 
 * @param {TypeRecord[] & { location: Location }} left 
 * @param {TypeRecord[] & { location: Location }} right 
 * @param {Location} location 
 * @param {TypeSystem} typeSystem 
 * @returns {TypeRecord}
 */
function minusOp(left, right, location, typeSystem) {
    if (!constrainNumeric(left, "left", location, typeSystem)) return typeSystem["undefined"];
    if (!constrainNumeric(right, "right", location, typeSystem)) return typeSystem["undefined"];

    return typeSystem["number"];
}

/**
 * 
 * @param {TypeRecord & { location: Location }} left 
 * @param {TypeRecord & { location: Location }} right 
 * @param {Location} location 
 * @param {TypeSystem} typeSystem 
 * @returns {TypeRecord}
 */
function timesOp(left, right, location, typeSystem) {
    if (!constrainNumeric(left, "left", location, typeSystem)) return typeSystem["undefined"];
    if (!constrainNumeric(right, "right", location, typeSystem)) return typeSystem["undefined"];

    return typeSystem["number"];
}

/**
 * 
 * @param {TypeRecord & { location: Location }} left 
 * @param {TypeRecord & { location: Location }} right 
 * @param {Location} location 
 * @param {TypeSystem} typeSystem 
 * @returns {TypeRecord}
 */
function divideOp(left, right, location, typeSystem) {
    if (!constrainNumeric(left, "left", location, typeSystem)) return typeSystem["undefined"];
    if (!constrainNumeric(right, "right", location, typeSystem)) return typeSystem["undefined"];

    return typeSystem["number|undefined"];
}

/**
 * 
 * @param {TypeRecord & { location: Location }} left 
 * @param {TypeRecord & { location: Location }} right 
 * @param {Location} location 
 * @param {TypeSystem} typeSystem 
 * @returns {TypeRecord}
 */
function modOp(left, right, location, typeSystem) {
    if (!constrainNumeric(left, "left", location, typeSystem)) return typeSystem["undefined"];
    if (!constrainNumeric(right, "right", location, typeSystem)) return typeSystem["undefined"];

    return typeSystem["number"];
}

/**
 * 
 * @param {TypeRecord & { location: Location }} left 
 * @param {TypeRecord & { location: Location }} right 
 * @param {Location} location 
 * @param {TypeSystem} typeSystem 
 * @returns {TypeRecord}
 */
function expOp(left, right, location, typeSystem) {
    if (!constrainNumeric(left, "left", location, typeSystem)) return typeSystem["undefined"];
    if (!constrainNumeric(right, "right", location, typeSystem)) return typeSystem["undefined"];

    return typeSystem["number"];
}

/**
 * 
 * @param {TypeRecord & { location: Location }} left 
 * @param {TypeRecord & { location: Location }} right 
 * @param {Location} location 
 * @param {TypeSystem} typeSystem 
 * @returns {TypeRecord}
 */
function plusOp(left, right, location, typeSystem) {
    if (definitelyString(left, location) || definitelyString(right, location)) return typeSystem["string"];
    
    else if(maybeString(left) || maybeString(right)) return { type: "union", types: ["string", "undefined", "number"] };
    else return typeSystem["number|undefined"];
}

/**
 * 
 * @param {TypeRecord[] & { location: Location }} type 
 * @param {string} relativeOperatorSide 
 * @param {Location} parentLocation
 * @param {TypeSystem} typeSystem
 * @returns {boolean}
 */
function constrainNumeric(type, relativeOperatorSide, parentLocation, typeSystem) {

    
    const hasNum = (type.length == 1 && type[0].type == "primitive" && type[0].primitive == "number");

    if(!hasNum) androidStudioLogging.sendTreeLocationMessage({
        text: `Uncheckable type mismatch on binary operator`,
        original: `This operator uses a numeric type, but the type checker could only promise \`${formatType(type, typeSystem)}\` for the ${relativeOperatorSide} side.\n` +
        `The ${relativeOperatorSide} side's value is defined at ` + formatLocation(type.location),
        kind: "WARNING",
        location: parentLocation
    });

    return hasNum;
}

/**
 * 
 * @param {TypeRecord[]} t 
 * @returns {boolean}
 */
function maybeString(t) {
    
    if(t.length == 1 && t[0].type == "primitive" && t[0].primitive === "string") return true;
    
    for(const type of t) {
        if(t.type == "primitive" && t.primitive == "string") return true;
    }
    return false;
}

/**
 * 
 * @param {TypeRecord[]} t 
 * @returns {boolean}
 */
function definitelyString(t) {
    return t.length == 1 && t[0].type == "primitive" && t[0].type === "string";
}