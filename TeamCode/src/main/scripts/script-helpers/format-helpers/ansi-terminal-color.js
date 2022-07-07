const CTRL = "\u001b[";
const RESET = `${CTRL}0m`;


module.exports = {
    colourString: colourString,
    getColourTag: getColourTag,
    reset: reset
}

function reset() {
    return RESET;
}

function colourString(colour, string) {    
    return `${getColourTag(colour)}${string}${RESET}`;
}

function getColourTag(colour) {
    return `${CTRL}38;5;${ rgb216(colour) }m`;
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