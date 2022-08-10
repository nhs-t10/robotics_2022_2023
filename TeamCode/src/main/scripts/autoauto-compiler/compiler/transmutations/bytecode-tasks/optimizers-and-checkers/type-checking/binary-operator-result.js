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
 * @param {TypeSystem} typeSystem
 * @returns {TypeRecord}
 */
function getBinaryOperatorResult(type, typeSystem) {
    
    const left = type.left, right = type.right;
    const leftRecord = typeSystem[left], rightRecord = typeSystem[right];
    
    const location = type.location;

    switch(type.op) {
        case "-": return minusOp(leftRecord, rightRecord, location, typeSystem);
        case "+": return plusOp(leftRecord, rightRecord, location, typeSystem);
        case "*": return timesOp(leftRecord, rightRecord, location, typeSystem);
        case "/": return divideOp(leftRecord, rightRecord, location, typeSystem);
        case "%": return modOp(leftRecord, rightRecord, location, typeSystem);
        case "**":
        case "^": return expOp(leftRecord, rightRecord, location, typeSystem);

        case ">":
        case ">=":
        case "==":
        case "!=":
        case "<=":
        case "<":
            return typeSystem["boolean"] || console.log("no boolean!");

        default: throw new Error("unknown operator " + op);
    }
}

/**
 * 
 * @param {TypeRecord} left 
 * @param {TypeRecord} right 
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
 * @param {TypeRecord} left 
 * @param {TypeRecord} right 
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
 * @param {TypeRecord} left 
 * @param {TypeRecord} right 
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
 * @param {TypeRecord} left 
 * @param {TypeRecord} right 
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
 * @param {TypeRecord} left 
 * @param {TypeRecord} right 
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
 * @param {TypeRecord} left 
 * @param {TypeRecord} right 
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
 * @param {TypeRecord} type 
 * @param {string} relativeOperatorSide 
 * @param {Location} parentLocation
 * @param {TypeSystem} typeSystem
 * @returns {boolean}
 */
function constrainNumeric(type, relativeOperatorSide, parentLocation, typeSystem) {
    
    
    const hasNum = (type.type == "primitive" && type.primitive == "number") ||
        (type.type == "union" && type.types.length == 1 && type.types[0] == "number") ||
        (type.type == "union" && type.types.length == 2 && type.types.includes("number" && type.types.includes("undefined")));

    if(!hasNum) androidStudioLogging.sendTreeLocationMessage({
        text: `Uncheckable type mismatch on binary operator`,
        original: `This operator uses a numeric type, but the type checker could only promise \`${formatType(type)}\` for the ${relativeOperatorSide} side.\n` +
        `The ${relativeOperatorSide} side's value is defined at ` + formatLocation(type.location),
        kind: "WARNING",
        location: parentLocation
    });

    return hasNum;
}

/**
 * 
 * @param {TypeRecord} t 
 * @returns {boolean}
 */
function maybeString(t) {
    return (t.type === "primitive" && t.primitive === "string") ||
        (t.type === "union" && t.types.includes("string"));
}

/**
 * 
 * @param {TypeRecord} t 
 * @returns {boolean}
 */
function definitelyString(t) {
    return ( t.type === "primitive" && t.primitive === "string") || 
        (t.type === "union" && t.types.length === 1 && t.types[0] === "string");
}