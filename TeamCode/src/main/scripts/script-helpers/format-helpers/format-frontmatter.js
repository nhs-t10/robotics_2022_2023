const ansiTerminalColor = require("./ansi-terminal-color");

module.exports = function formatFrontmatter(fm, props) {
    const lines = ["  $"];
    let longestLine = 0;
    for(const key in fm) {

        let tag = getTag(key, props);
        let reset = hasTag(key, props) ? ansiTerminalColor.reset() : "";

        let line = tag + "    " + key + ": " + JSON.stringify(fm[key]) + reset + ",";
        if(line.length > longestLine) longestLine = line.length;

        lines.push(line);
    }

    lines[lines.length - 1] = lines[lines.length - 1].slice(0, -1);
    lines.push("  $");

    const block = padToBlock(lines, longestLine);

    return block;
}

function padToBlock(lines, n) {
    var r = [];

    for(const l of lines) {
        r.push(padEnd(l, n));
    }
    return r.join("\n");
}

function padEnd(str, l) {
    while(str.length < l) str += " ";
    return str;
}

function hasTag(key, props) {
    return props && (key in props);
}

function getTag(key, props) {
    if(!props) return "  ";

    if(props[key] == "+") {
        return ansiTerminalColor.getColourTag("#00ff00") + "+ ";
    } else if(props[key] == "-") {
        return ansiTerminalColor.getColourTag("#ff0033") + "- ";
    } else if(props[key] == "/") {
        return ansiTerminalColor.getColourTag("#ffff33") + "/ ";
    } else {
        return "  ";
    }
}