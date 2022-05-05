var fs = require("fs");
var PngFile = require("./png-file.js");

var RENDER_WIDTH = 0xff_ff - 9;
var RENDER_HEIGHT = 1;

var pixels = [];
for(var i = 0; i < RENDER_HEIGHT; i++) {
    var row = [];
    for(var j = 0; j < RENDER_WIDTH; j++) {
        row.push(color(i, j));
    }
    pixels.push(row);
}

var png = new PngFile(pixels, RENDER_WIDTH);
//for(var i = 0; i < 1666; i++) png.addOptionalChunk("TEXT", makeFullTextChunk());

fs.writeFileSync(__dirname + "/test.png", png.toBuffer()); //SAFE

function makeFullTextChunk() {
    var c = String.fromCharCode(Math.floor(Math.random() * 26) + 65);
    return Buffer.concat([
        Buffer.from(c, "ascii"),
        Buffer.from([0]),
        Buffer.from(c.repeat(0xff_ff - 9), "ascii")
    ]);
}

function color(y, x) {
    return parseHex("#ffffff");
}

function parseHex(hex) {
    hex = hex.replace(/^#/, "");
    return [
        parseInt(hex.substring(0, 2), 16),
        parseInt(hex.substring(2, 4), 16),
        parseInt(hex.substring(4, 6), 16)
    ];
}