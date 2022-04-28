const androidStudioLogging = require("../../../../../../script-helpers/android-studio-logging");

module.exports = function getBinaryOperatorResult(left, right, op, filename, location) {
    if(typeof left == "string") left = [left];

    if(typeof right == "string") right = [right];

    switch(op) {
        case "-": return minusOp(left, right, filename, location);
        case "+": return plusOp(left, right, filename, location);
        case "*": return timesOp(left, right, filename, location);
        case "/": return divideOp(left, right, filename, location);
        case "%": return modOp(left, right, filename, location);
        case "**":
        case "^": return expOp(left, right, filename, location);

        case ">":
        case ">=":
        case "==":
        case "!=":
        case "<=":
        case "<":
            return "boolean";

        default: throw new Error("unknown operator " + op);
    }
}

function minusOp(left, right, filename, location) {
    if(!constrainNumeric(left, filename, location, "left")) return "undefined";
    if(!constrainNumeric(right, filename, location, "right")) return "undefined";

    return "number";
}

function timesOp(left, right, filename, location) {
    if(!constrainNumeric(left, filename, location, "left")) return "undefined";
    if(!constrainNumeric(right, filename, location, "right")) return "undefined";

    return "number";
}
function divideOp(left, right, filename, location) {
    if(!constrainNumeric(left, filename, location, "left")) return "undefined";
    if(!constrainNumeric(right, filename, location, "right")) return "undefined";

    return ["undefined","number"];
}
function modOp(left, right, filename, location) {
    if(!constrainNumeric(left, filename, location, "left")) return "undefined";
    if(!constrainNumeric(right, filename, location, "right")) return "undefined";

    return "number";
}
function expOp(left, right, filename, location) {
    if(!constrainNumeric(left, filename, location, "left")) return "undefined";
    if(!constrainNumeric(right, filename, location, "right")) return "undefined";

    return "number";
}

function plusOp(left, right, filename, location) {
    if(definitelyString(left) || definitelyString(right)) return "string";

    else if(maybeString(left) || maybeString(right)) return ["string", "undefined", "number"];
    else return ["undefined", "number"];
}

function constrainNumeric(type, filename, location, relativeOperatorSide) {
    var hasNum = type.length == 1 && type[0] == "number"
        || type.length == 2 && type.indexOf("number") != -1 && type.indexOf("undefined") != -1;

    if(typeof type == "object") {
        type = type.type;
        if(type == "object") type = "table";
        type = [type];
    }

    if(!hasNum) androidStudioLogging.sendTreeLocationMessage({
        text: `Uncheckable type mismatch on binary operator`,
        original: `This operator uses a numeric type, but the type checker could only promise \`${type.join("|")}\` for the ${relativeOperatorSide} side.`,
        kind: "WARNING",
        location: location
    }, filename)

    return hasNum;
}

function maybeString(t) {
    return "indexOf" in t && t.indexOf("string") != -1;
}

function definitelyString(t) {
    return t.length == 1 && t[0] == "string";
}