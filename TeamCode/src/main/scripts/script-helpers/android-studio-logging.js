"use strict";

//https://github.com/zawn/android-gradle-plugin-src/blob/master/sdk-common/src/main/java/com/android/ide/common/blame/MessageJsonSerializer.java

const cFs = require("./cached-fs");
const commandLineArguments = require("../command-line-interface");
const errorResolutionSuggestions = require("./error-resolution-suggestions");
const { colourString } = require("./format-helpers/ansi-terminal-color");
const { sha } = require("./sha-string");

module.exports = {
    sendTreeLocationMessage: sendTreeLocationMessage,

    warning: sendWarn,
    error: sendError,

    beginOutputCapture: beginOutputCapture,
    getCapturedOutput: getCapturedOutput,
    isCapturingOutput: isCapturingOutput,

    sendMessages: sendMessages,

    printTypeCounts: printTypeCounts,
    resetTypeCounts: resetTypeCounts,

    sendInternalError: sendInternalError,

    getGlobalState: getGlobalState,
    setGlobalState: setGlobalState,
    
    printTimingInformation: printTimingInformation,
    
    addAppendix: addAppendix,
    printAppendixes: printAppendixes
};



const COLOURS = {
    WARNING: "#999900",
    BARELY_WARNING: "#669900",
    ERROR: "#ff0033",
    INFO: "#6666aa",
    UNKNOWN_MESSAGE_KIND: "#99cccc",
    HINT: "#cc66cc",
    LINE_NUMBER: "#666666",
    NUMBER: "#3333ff"
}

var capturingOutput = false;
var captured = [];

const DEFAULT_ZERO_COUNTS = {
    ERROR: 0,
    WARNING: 0,
    INFO: 0,
    BARELY_WARNING: 0
};

var appendices = new Set();
var counts = Object.assign({}, DEFAULT_ZERO_COUNTS);

const logLevel = commandLineArguments.quiet ? 3 : 0;

function getGlobalState() {
    var _cO = capturingOutput;
    var _cd = captured;
    return function () {
        return {
            cO: _cO,
            cd: _cd
        };
    }
}

function setGlobalState(s) {
    var r = s();
    capturingOutput = r.cO;
    captured = r.cd;
}

function beginOutputCapture() {
    capturingOutput = true;
    captured = [];
}

function isCapturingOutput() {
    return capturingOutput;
}

function getCapturedOutput() {
    capturingOutput = false;
    const t = captured;
    captured = [];
    return t;
}

function sendWarn(msgStr) {
    sendTreeLocationMessage({
        kind: "WARNING",
        text: msgStr
    });
}

function sendError(msgStr) {
    sendTreeLocationMessage({
        kind: "ERROR",
        text: msgStr
    });
}

function printAppendixes() {
    if(commandLineArguments["no-appendix"]) return;
    
    for (const apx of appendices) sendRawText(apx);
}

function addAppendix(appendixText) {
    var appendixTitle = sha(appendixText).substring(0, 7);
    sendPlainMessage({
        appendix: "Appendix " + appendixTitle + "\n===\n" + appendixText,
        kind: "BARELY_WARNING"
    });
    
    return appendixTitle;
}

function sendMessages(msgs) {
    msgs.forEach(x => sendPlainMessage(x));
}

function sendPlainMessage(msg) {
    const l = ["INFO", "BARELY_WARNING", "WARNING", "ERROR"].indexOf(msg.kind);

    if (capturingOutput) {
        captured.push(msg);
    } else {
        incrementTypeCount(msg.kind);

        if (logLevel <= l || l === -1) {
            if (capturingOutput) {
                captured.push(msg);
            } else if(typeof msg.appendix === "string") {
                appendices.add(msg.appendix);
            } else {                
                if (commandLineArguments["agpbi"]) formatAndSendJsonFormat(msg);

                formatAndSendHumanyFormat(msg);
            }
        }
    }
}

function incrementTypeCount(kind) {
    kind = ("" + kind).toUpperCase();

    if (kind in counts) counts[kind]++;
}

function printTypeCounts() {
    const cE = counts.ERROR;
    const cW = counts.WARNING;
    const cBW = counts.BARELY_WARNING;
    const cAW = cW + cBW;
    const cI = counts.INFO;
    sendRawText(
        "Compilation finished. " +
        colourString(COLOURS.ERROR, cE + maybePlural(cE, " error")) + ", " +
        colourString(COLOURS.WARNING, cAW + maybePlural(cAW, " warning")) + " (" +
        colourString(COLOURS.BARELY_WARNING, counts.BARELY_WARNING + " barely") + "), " +
        colourString(COLOURS.INFO, cI + " informational")
    );
}

function printTimingInformation(filesCompiled, timeMs) {
    const roundedSeconds = Math.round(timeMs / 1000 * 1e5) / 1e5;
    
    const timePerFile = roundedSeconds / filesCompiled;
    const failedTimeGoodness = timePerFile > 1;
    const timeColour = failedTimeGoodness ? COLOURS.ERROR : COLOURS.BARELY_WARNING;
    
    
    sendRawText(
        colourString(COLOURS.NUMBER, filesCompiled + " files") + 
        " processed; took " +
        colourString(timeColour, roundedSeconds + " seconds") + " in total"
    );
}

function resetTypeCounts() {
    Object.assign(counts, DEFAULT_ZERO_COUNTS);
}

function maybePlural(num, word) {
    if (num == 1) return word;
    else return word + "s";
}

function formatAndSendJsonFormat(msg) {
    const f = Object.assign({}, msg);

    if (f.kind == "BARELY_WARNING") f.kind = "WARNING";
    f.original = humanReadableFormat(msg);

    sendRawText("AGPBI: " + JSON.stringify(f));
}

function formatAndSendHumanyFormat(msg) {
    sendRawText(humanReadableFormat(msg));
}

function sendRawText(txt) {
    console.info(txt);
}

function humanReadableFormat(msg) {
    var mForm = "";

    if (msg.sources) {
        if (msg.sources[0]) {
            if (msg.sources[0].file) mForm += msg.sources[0].file + ":";

            if (msg.sources[0].location) {
                mForm += msg.sources[0].location.startLine + ":";
            }
            if (mForm != "") mForm += " ";
        }
    }

    mForm += msg.kind.toLowerCase() + ": ";

    mForm += singleLine(msg.text) + ":\n" + indent("  ", msg.original || "");

    mForm += "\n\n";

    return mForm;
}

function singleLine(t) {
    return t.replace(/\n/g, " ");
}

function indent(indentBy, t) {
    return t.split("\n").map(x => indentBy + x).join("\n");
}

/**
 * 
 * @param {Partial<CompilerMessage} res 
 * @param {string?} file 
 * @param {string?} defaultKind 
 */
function sendTreeLocationMessage(res, file, defaultKind) {
    sendMessages(massageResIntoArrayOfMessages(res, file, defaultKind));
}

function sendInternalError(err, file) {
    if (err === undefined || err === null) return sendInternalError(new Error("undefined internal error"));

    if (err instanceof Error) {
        sendTreeLocationMessage({
            kind: "ERROR",
            text: "Internal Compiler Error",
            original: `There was an internal error. This file will be skipped, but others will still be compiled.\n` +
                `Please contact these people in this order: \n` +
                `1) Connor\n` +
                `2) Chloe\n` +
                `\n` +
                `The stack of the error is below:\n` +
                err.message + "\n" + err.stack
        }, file, "ERROR");
    } else {
        sendTreeLocationMessage(err, file, "ERROR");
    }
}


/**
 * 
 * @param {Partial<CompilerMessage> | Partial<CompilerMessage>[]} res 
 * @param {string?} file 
 * @param {string?} defaultKind 
 * @returns {AndroidStudioMessage[]}
 */
function massageResIntoArrayOfMessages(res, file, defaultKind) {
    if (res.constructor === Array) return res.map(x => massageResIntoMessage(x, file, defaultKind));
    else return [massageResIntoMessage(res, file, defaultKind)];
}

/**
 * @typedef {object} CompilerMessage
 * @property {"INFO" | "WARNING" | "BARELY_WARNING" | "ERROR"} kind
 * @property {string} text
 * @property {string} original
 * @property {string[]} hints
 * @property {string?} appendix
 * @property {import("../autoauto-compiler/compiler/transmutations/text-to-syntax-tree/parser").Location} location
 */

/**
 * @typedef {object} AndroidStudioMessage
 * @property {"INFO" | "WARNING" | "BARELY_WARNING" | "ERROR"} kind
 * @property {string} text
 * @property {string} original
 * @property {AgpbiSource[]} sources
 */

/**
 * @typedef {object} AgpbiSource
 * @property {string} file
 * @property {AgpbiCursor} location
 */

/**
 * @typedef {object} AgpbiCursor 
 * @property {number} startLine
 * @property {number} startColumn
 * @property {number} startOffset
 * @property {number} endLine
 * @property {number} endColumn
 * @property {number} endOffset
 */

/**
 * 
 * @param {Partial<CompilerMessage> | string} res 
 * @param {string?} file 
 * @param {string?} defaultKind 
 * @returns {AndroidStudioMessage}
 */
function massageResIntoMessage(res, file, defaultKind) {
    if (typeof res === "string") res = { text: res };

    if (defaultKind === undefined) defaultKind = "INFO";

    if (!res.kind) res.kind = defaultKind;

    if (!res.original) res.original = "";

    if (res.location && res.location.file) file = res.location.file;

    if (res instanceof Error) {
        res = {
            kind: "ERROR",
            text: res.toString(),
            original: res.toString() + ":\n"
                + errorResolutionSuggestions(res)
                + "\n" + res.stack,
            location: res.location
        }
    }

    if (res.fail) res.original += " | Skipping File";

    if (res.sources === undefined) {
        res.sources = [{
            file: file
        }];
    }
    if (res.location && !res.sources[0].location) {
        res.sources[0].location = {
            startLine: -1,
            startColumn: -1,
            startOffset: -1,
            endLine: -1,
            endColumn: -1,
            endOffset: -1,
        };


        if (typeof res.location.start === "object") {
            Object.assign(res.sources[0].location, {
                startLine: res.location.start.line,
                startColumn: res.location.start.column,
                startOffset: res.location.start.offset
            });
        }
        if (typeof res.location.end === "object") {
            Object.assign(res.sources[0].location, {
                endLine: res.location.end.line,
                endColumn: res.location.end.column,
                endOffset: res.location.end.offset
            });
        }

        res.original = formatPointerToCode(file, res.location, res.kind, res.text, res.hints || []) + "\n" + res.original;
        delete res.location;
    }
    return res;
}

function formatPointerToCode(file, location, kind, label, hints) {
    if (!file || !location.start || !location.end) return "";

    var fileStack = formatFileStack(location.fileStack);

    var fContent = cFs.readFileSync(file).toString();

    var lineStart = fContent.lastIndexOf("\n", location.start.offset);
    var lineEnd = fContent.indexOf("\n", location.end.offset);

    //give one line of context on either side, if there's space
    if (lineStart != -1) lineStart = fContent.lastIndexOf("\n", lineStart - 1);
    if (lineStart < 0) lineStart = 0;

    if (lineEnd != -1) lineEnd = fContent.indexOf("\n", lineEnd + 1);
    if (lineEnd < 0) lineEnd = fContent.length;

    var snippet = fContent.substring(lineStart, lineEnd);

    //calculate index of selection within the substring
    var selectionStart = location.start.offset - lineStart;
    var selectionEnd = location.end.offset - lineStart;

    var colour = getKindColour(kind);

    var selBeg = snippet.substring(1, selectionStart);
    var sel = snippet.substring(selectionStart, selectionEnd);
    var selEnd = snippet.substring(selectionEnd);

    var selected = selBeg + colourString(colour, sel) + selEnd;

    var selectedWithRowNumbers = addRowNumbers(selected, location.start.line - 1);
    var rowNumberWidth = (location.start.line + 1).toString().length + 3;

    var rowWidthUntilSelected = selectionStart - (snippet.lastIndexOf("\n", selectionStart) + 1);
    var margin = " ".repeat(rowWidthUntilSelected + rowNumberWidth);

    var pointer = margin + colourString(colour, "^ " + label);

    var hintText = hints.map(x => `\n${margin}  ${colourString(COLOURS.HINT, x)}`).join("");

    return fileStack + selectedWithRowNumbers + "\n" + pointer + hintText;
}

function formatFileStack(stack) {
    if (stack === undefined) return "";

    const arrow = commandLineArguments.ascii ? "<--" : "\u250c\u2500\u2500";

    var result = "";
    var pad = " ";
    for (const file of stack) {
        result = pad + arrow + file + "\n" + result;
        pad += " ";
    }
    return result;
}

function getKindColour(kind) {
    return COLOURS[kind] || COLOURS.UNKNOWN_MESSAGE_KIND;
}

function addRowNumbers(text, startRow) {

    var rows = extractColorFromRows(text);

    var maxRow = startRow + rows.length - 1;
    var w = (maxRow + "").length;

    var lineCharacter = commandLineArguments.ascii ? "\u2502" : "|";

    return rows
        .map((x, i) => {
            var rN = startRow + i;
            return colourString(COLOURS.LINE_NUMBER, `${pad(rN, w)} ${lineCharacter} `) + x.replace(/\r/g, "");
        })
        .join("\n");
}

function pad(txt, width) {
    txt += "";

    while (txt.length < width) txt = " " + txt;

    return txt;
}

function extractColorFromRows(string) {
    var colourCodeStack = [];
    var rows = [];
    var row = "";
    var tag = "";

    for (var i = 0; i < string.length; i++) {
        if (string.startsWith("\u001b[", i)) {
            var end = string.indexOf("m", i);
            tag = string.substring(i, end) + "m";
            i = end;
            if (tag == "\u001b[0m") colourCodeStack.pop();
            else colourCodeStack.push(tag);
            row += tag;
        } else if (string[i] == "\n") {
            var r = colourCodeStack.join("") + row + "\u001b[0m";
            row = "";
            rows.push(r);
        } else {
            if (string[i] != "\r") row += string[i];
        }
    }
    rows.push(colourCodeStack.join("") + row + "\u001b[0m");

    return rows;
}