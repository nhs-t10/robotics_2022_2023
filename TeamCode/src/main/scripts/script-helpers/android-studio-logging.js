//https://github.com/zawn/android-gradle-plugin-src/blob/master/sdk-common/src/main/java/com/android/ide/common/blame/MessageJsonSerializer.java

var cFs = require("./cached-fs");
const commandLineArguments = require("../command-line-interface");

module.exports = {
    sendPlainMessage: sendPlainMessage,
    sendTreeLocationMessage: sendTreeLocationMessage,
    
    warning: sendWarn,

    beginOutputCapture: beginOutputCapture,
    getCapturedOutput: getCapturedOutput,

    sendMessages: sendMessages,
    
    printTypeCounts: printTypeCounts
};



const COLOURS = {
    WARNING: "#999900",
    BARELY_WARNING: "#669900",
    ERROR: "#ff0033",
    INFO: "#6666aa",
    UNKNOWN_MESSAGE_KIND: "#99cccc",
    HINT: "#cc66cc",
    LINE_NUMBER: "#666666"
}

var capturingOutput = false;
var captured = [];

var counts = {
    ERROR: 0,
    WARNING: 0,
    INFO: 0,
    BARELY_WARNING: 0
};
const logLevel = commandLineArguments.quiet ? 3 : 0;

function beginOutputCapture() {
    capturingOutput = true;
    captured = [];
}

function getCapturedOutput() {
    capturingOutput = false;
    var t = captured;
    captured = [];
    return t;
}

function sendWarn(msgStr) {
    sendPlainMessage({
        kind: "WARNING",
        text: msgStr
    });
}

function sendMessages(msgs) {
    msgs.forEach(x=>sendPlainMessage(x));
}

function sendPlainMessage (msg) {
    var l = ["INFO", "BARELY_WARNING", "WARNING","ERROR"].indexOf(msg.kind);
    
    incrementTypeCount(msg.kind);
    
    if (logLevel <= l || l === -1) {
        if(capturingOutput) {
            captured.push(msg);
        } else {            
            formatAndSendJsonFormat(msg);
            formatAndSendHumanyFormat(msg);
        }
    }
}

function incrementTypeCount(kind) {
    kind = ("" + kind).toUpperCase();
    
    if(kind in counts) counts[kind]++
}

function printTypeCounts() {
    sendRawText(
        "Compilation finished. " +
        colourString(COLOURS.ERROR, counts.ERROR + " errors") + ", " +
        colourString(COLOURS.WARNING, (counts.WARNING + counts.BARELY_WARNING) + " warnings") + " (" +
        colourString(COLOURS.BARELY_WARNING, counts.BARELY_WARNING + " barely") + "), " +
        colourString(COLOURS.INFO, counts.INFO + " informational")
    );
}

function formatAndSendJsonFormat(msg) {
    var f = Object.assign({}, msg);

    if(f.kind == "BARELY_WARNING") f.kind = "WARNING";
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

    if (msg.sources[0]) {
        mForm += msg.sources[0].file + ":";
        if (msg.sources[0].location) {
            mForm += msg.sources[0].location.startLine + ":";
        }
        mForm += " ";
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
    return t.split("\n").map(x=>indentBy + x).join("\n");
}

function sendTreeLocationMessage(res, file, defaultKind) {
    massageResIntoArrayOfMessages(res, file, defaultKind).forEach(x=>sendPlainMessage(x));
}


function massageResIntoArrayOfMessages(res, file, defaultKind) {
    if(res.constructor === Array) return res.map(x=>massageResIntoMessage(x, file, defaultKind));
    else return [massageResIntoMessage(res, file, defaultKind)];
}

function massageResIntoMessage(res, file, defaultKind) {
    if(typeof res === "string") res = { text: res };
    
    if(!res.kind) res.kind = defaultKind;
    
    if(!res.original) res.original = "";
    
    if(res instanceof Error) {
        res = {
            kind: "ERROR",
            text: res.toString(),
            original: res.toString() + "\n" + res.stack,
            location: res.location
        }
    }

    if(res.fail) res.text += " | Skipping File";

    if(res.sources === undefined) {
            res.sources = [{
            file: file
        }];
    }
    if(res.location && !res.sources[0].location) {
        res.sources[0].location = {
            startLine: res.location.start.line,
            startColumn: res.location.start.column,
            startOffset: res.location.start.offset,
            endLine: res.location.end.line,
            endColumn: res.location.end.line,
            endOffset: res.location.end.offset
        };
        res.original = formatPointerToCode(file, res.location, res.kind, res.text, res.hints || []) + "\n" + res.original;
        delete res.location;
    }
    return res;
}

function formatPointerToCode(file, location, kind, label, hints) {
    if(!file) return "";
    
    var fContent = cFs.readFileSync(file).toString();
    
    var lineStart = fContent.lastIndexOf("\n", location.start.offset);
    var lineEnd = fContent.indexOf("\n", location.end.offset);
    
    //give one line of context on either side, if there's space
    if (lineStart != -1) lineStart = fContent.lastIndexOf("\n", lineStart - 1);
    if(lineStart < 0) lineStart = 0;
    
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
    
    return selectedWithRowNumbers + "\n" + pointer +  hintText;
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
        .map((x,i)=> {
            var rN = startRow + i;
            return colourString(COLOURS.LINE_NUMBER, `${pad(rN, w)} ${lineCharacter} `) + x.replace(/\r/g, "");
        })
        .join("\n");
}

function pad(txt, width) {
    txt += "";
    
    while(txt.length < width) txt = " " + txt;
    
    return txt;
}

function extractColorFromRows(string) {
    var colourCodeStack = [];
    var rows = [];
    var row = "";
    
    for(var i = 0; i < string.length; i++) {
        if(string.startsWith("\033[", i)) {
            var end = string.indexOf("m", i);
            tag = string.substring(i, end) + "m";
            i = end;
            if(tag == "\033[0m") colourCodeStack.pop();
            else colourCodeStack.push(tag);
            row += tag;
        } else if(string[i] == "\n") {
            var r = colourCodeStack.join("") + row + "\033[0m";
            row = "";
            rows.push(r);
        } else {
            if(string[i] != "\r") row += string[i];
        }
    }
    rows.push(colourCodeStack.join("") + row + "\033[0m");
    
    return rows;
}

function colourString(colour, string) {
    var ctrl = "\033[";
    var cCode = `${ctrl}38;5;${ rgb216(colour) }m`;
    var rCode = `${ctrl}0m`;
    
    return `${cCode}${string}${rCode}`;
}

function rgb216(r, g, b) {
    if (typeof r === "string") {
        r = r.replace("#", "");

        b = (parseInt(r.substring(4, 6), 16) / 0xff) || 0;
        g = (parseInt(r.substring(2, 4), 16) / 0xff) || 0;
        r = (parseInt(r.substring(0, 2), 16) / 0xff) || 0;
    }
    var rf = Math.round(r * 5), gf = Math.round(g * 5), bf = Math.round(b * 5);
    return (rf * 36) + (gf * 6) + (bf) + 16
}