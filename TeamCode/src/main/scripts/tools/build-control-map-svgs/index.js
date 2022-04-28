var makeMapSvg = require("./make-svg");
var loadControlMaps = require("./load-control-maps");

var controlMaps = loadControlMaps();

Object.entries(controlMaps).forEach(x=>makeMapSvg(x));