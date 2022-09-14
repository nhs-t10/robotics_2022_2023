const path = require("path");
const androidStudioLogging = require("../../../../../../script-helpers/android-studio-logging");

module.exports = {
    formatType: formatType,
    formatLocation: formatLocation,
    shortRelativeFormatLocation: shortRelativeFormatLocation
}

/**
 * @typedef {import("./verify-type-system").TypeRecord} TypeRecord
 * @typedef {import("./verify-type-system").TypeSystem} TypeSystem
 */

/**
 * 
 * @param {TypeRecord | TypeRecord[]} type 
 * @param {import("./verify-type-system").TypeSystem} typeSystem 
 * @param {number | undefined} indentation 
 * @param {boolean | undefined} allowMultiline
 * @returns {string}
 */
function formatType(type, typeSystem, indentation, allowMultiline) {

    if (Array.isArray(type)) return type.map(x => formatType(x, typeSystem, indentation)).join(" | ");

    indentation |= 0;

    switch (type.type) {
        case "?": return "unknown";
        case "primitive": return type.primitive;
        case "function": return formatFunction(type, typeSystem, indentation, allowMultiline);
        case "object": return formatTableRecord(type, typeSystem, indentation, allowMultiline);
        case "union": return formatUnion(type, typeSystem, indentation, allowMultiline);
    }
    return "<unformatted type " + type.type + ">"
}

/**
 * 
 * @param {import("./verify-type-system").UnionType} type 
 * @param {TypeSystem} typeSystem 
 * @param {number} indentation 
 * @param {boolean} allowMultiline 
 */
function formatUnion(type, typeSystem, indentation, allowMultiline) {
    
    const indentText = "  ".repeat(indentation);
    const joinText = "\n" + indentText + "| ";
    
    const types = type.types.map(x => typeSystem[x]);
    
    if(types.length == 0) return "<empty type; nothing>";
    if(types.length == 1) return formatType(types[0], typeSystem, indentation, allowMultiline);
    
    if(allPrimitive(types)) return type.types.join(" | ");
    
    return types.map(x => formatType(x, typeSystem, indentation + 1, allowMultiline)).join(allowMultiline ? joinText : " | ")
}

/**
 * 
 * @param {TypeRecord[]} types 
 */
function allPrimitive(types) {
    for(const t of types) if(t.type != "primitive") return false;
    return true;
}

/**
 * 
 * @param {import("./verify-type-system").FunctionType} type 
 * @param {TypeSystem} typeSystem 
 * @param {number} indentation 
 * @param {boolean} allowMultiline 
 */
function formatFunction(type, typeSystem, indentation, allowMultiline) {
    const indentatText = "  ".repeat(indentation);
    let text = "function(";
    if (allowMultiline && type.args.length > 0) text += "\n";
    
    const functionPartsIndentLength = "func".length + indentation;
    const partIndent = "  ".repeat(functionPartsIndentLength);

    for (let i = 0; i < type.args.length; i++) {
        const argType = typeSystem[type.args[i]];
        const argName = type.argnames[i] || String.fromCharCode(97 + i);

        if (allowMultiline) text += partIndent;
        
        text += argName + ": " + formatType(argType, typeSystem, functionPartsIndentLength, allowMultiline);
        
        if(type.args.length > i + 1) text += ","; 
        if (allowMultiline) text += "\n";  
    }

    if (type.varargs != undefined) {
        const varargs = formatType(typeSystem[type.varargs], typeSystem, functionPartsIndentLength, allowMultiline);
        if (varargs != "undefined") {
            text += allowMultiline ?  partIndent : " ";
            
            text += "..." + varargs;
            if (allowMultiline) text += "\n";
        }
    }

    if (allowMultiline && type.args.length > 0) text += indentatText;
    text += ") -> " + formatType(typeSystem[type.return], typeSystem, functionPartsIndentLength, allowMultiline);
    
    return text;
}

/**
 * 
 * @param {import("./verify-type-system").TableType} type 
 * @param {TypeSystem} typeSystem 
 * @param {number} indentation 
 * @param {boolean} allowMultiline
 * @returns {string}
 */
function formatTableRecord(type, typeSystem, indentation, allowMultiline) {
    let text = "";
    for (const key in type.properties) {
        const typeName = type.properties[key];
        const typeRecord = typeSystem[typeName];

        text += "  ".repeat(indentation) + " - " + key + ": " + formatType(typeRecord || typeName, typeSystem, indentation + 1, true) + ",\n";
    }

    if (text === "") {
        return "a table with no properties";
    } else if (allowMultiline) {
        return "a table with:\n" + text;
    } else {
        const appendixId = androidStudioLogging.addAppendix(text);
        return "a table (exact format in appendix " + appendixId + ")";
    }

}

/**
 * 
 * @param {import("../../../text-to-syntax-tree/parser").Location} location 
 */
function formatLocation(location) {
    if (location === undefined) return "<built-in>";
    else if(location.synthetic) return location.file;
    else return location.file + ":" + location.start.line + ":" + location.start.column;
}

/**
 * 
 * @param {import("../../../text-to-syntax-tree/parser").Location} location 
 */
function shortRelativeFormatLocation(location) {
    if (location === undefined) return "<built-in>";
    var fileBasename = path.basename(location.file);
    
    if(location.synthetic) return fileBasename;
    else return fileBasename + ":" + location.start.column;
}