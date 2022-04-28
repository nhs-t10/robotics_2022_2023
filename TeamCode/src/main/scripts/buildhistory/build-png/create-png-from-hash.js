var fs = require("fs");
var path = require("path");

var pixelsToMatrix = require("./pixels-to-matrix.js");

var savePng = require("./save-png");
var PngFile = require("./png-file/png-file");

var PHI = 1.61803399;


module.exports = function(buildNumber, hash, pixelStrategySeed) {

    var pixels = getHexPixels(hash);
    var normalizedPixels = normalizePixels(pixels);

    var matrix = pixelsToMatrix(normalizedPixels, pixelStrategySeed);
    
    if(matrix.length == 0) return null;
    
    try {
        makeSureMatrixIsSquare(matrix);
    } catch(e) {
        console.warn(e);
        return null;
    }

    var naturalMatrixWidth = matrix[0].length;
    var renderedWidth = naturalMatrixWidth;

    if(naturalMatrixWidth > 1024) renderedWidth = 1024;
    if(naturalMatrixWidth < 128) renderedWidth = 128;
    
    var png = new PngFile(matrix, renderedWidth);
    
    var nonzeroBuildAddress = savePng(buildNumber, png.toBuffer());
    
    return {
        address: nonzeroBuildAddress,
        colors: normalizedPixels.map(x=>pixelToHex(x)).join(",")
    };
};

function makeSureMatrixIsSquare(matrix) {
    var width = matrix[0].length;
    for(var i = 0; i < matrix.length; i++) {
        if(matrix[i].length != width) throw "Non-rectangular matrix!";
        
        for(var j = 0; j < width; j++) {
            if(matrix[i][j] == undefined || matrix[i][j].length != 3) throw "Non-pixel matrix element! " + matrix[i][j];
        }
    }
}

function normalizePixels(pixels) {
    var hsvPixels = pixels.map(x=>RGBtoHSV(x[0], x[1], x[2]));
    
    var brightnesses = hsvPixels.map(x=>x[2]);
    var highestBrightness = Math.max(...brightnesses);
    
    //only scale things up to 180 brightness, not the full 255 bc that'll make things washed out
    var highestSaturatedBrightness = highestBrightness * 2;
    
    //sanity check to make sure it's never above 1
    var scaledBrightnesses = brightnesses.map(x=> Math.min(1, x / highestSaturatedBrightness));
    
    var scaledPixels = hsvPixels.map((x,i,a)=>[
        nextPhiHue(x[0], a[i - 1], a[i - 2]),
        0.25 + (Math.sqrt(x[1]) * (1 / 0.75)),
        1 - scaledBrightnesses[i]]);
    
    var rgbScaled = scaledPixels.map(x=>HSVtoRGB(x[0], x[1], x[2]));
    
    return rgbScaled;
    
}

function nextPhiHue(currentHue, lastPixel1, lastPixel2) {
    if(!lastPixel1 || !lastPixel2) return currentHue;
    
    var lastHue1 = lastPixel1[0], lastHue2 = lastPixel2[0];
    
    var hueDifference = lastHue2 - lastHue1;
    var hueOffset = hueDifference * PHI;
    
    var nextHue = lastHue1 + hueOffset;
    
    return (nextHue + 2) % 1;
}


function getHexPixels(str) {
    var pixels = [];
    
    str = str + "";
    for(var i = 0; i < str.length; i+=6) {
        pixels.push([
            parseInt(str.substring(i + 0, i + 2), 16) || 0,
            parseInt(str.substring(i + 2, i + 4), 16) || 0,
            parseInt(str.substring(i + 4, i + 6), 16) || 0
        ]);
    }
    return pixels;
}
function HSVtoRGB(h, s, v) {
    var r, g, b, i, f, p, q, t;
    if (arguments.length === 1) {
        s = h.s, v = h.v, h = h.h;
    }
    i = Math.floor(h * 6);
    f = h * 6 - i;
    p = v * (1 - s);
    q = v * (1 - f * s);
    t = v * (1 - (1 - f) * s);
    switch (i % 6) {
        case 0: r = v, g = t, b = p; break;
        case 1: r = q, g = v, b = p; break;
        case 2: r = p, g = v, b = t; break;
        case 3: r = p, g = q, b = v; break;
        case 4: r = t, g = p, b = v; break;
        case 5: r = v, g = p, b = q; break;
    }
    return {
        r: Math.round(r * 255),
        g: Math.round(g * 255),
        b: Math.round(b * 255)
    };
}
function RGBtoHSV(r, g, b) {
    if (arguments.length === 1) {
        g = r.g, b = r.b, r = r.r;
    }
    var max = Math.max(r, g, b), min = Math.min(r, g, b),
        d = max - min,
        h,
        s = (max === 0 ? 0 : d / max),
        v = max / 255;

    switch (max) {
        case min: h = 0; break;
        case r: h = (g - b) + d * (g < b ? 6: 0); h /= 6 * d; break;
        case g: h = (b - r) + d * 2; h /= 6 * d; break;
        case b: h = (r - g) + d * 4; h /= 6 * d; break;
    }

    return [h,s,v];
}
/* accepts parameters
 * h  Object = {h:x, s:y, v:z}
 * OR 
 * h, s, v
*/
function HSVtoRGB(h, s, v) {
    var r, g, b, i, f, p, q, t;
    if (arguments.length === 1) {
        s = h.s, v = h.v, h = h.h;
    }
    i = Math.floor(h * 6);
    f = h * 6 - i;
    p = v * (1 - s);
    q = v * (1 - f * s);
    t = v * (1 - (1 - f) * s);
    switch (i % 6) {
        case 0: r = v, g = t, b = p; break;
        case 1: r = q, g = v, b = p; break;
        case 2: r = p, g = v, b = t; break;
        case 3: r = p, g = q, b = v; break;
        case 4: r = t, g = p, b = v; break;
        case 5: r = v, g = p, b = q; break;
    }
    return [
        Math.round(r * 255),
        Math.round(g * 255),
        Math.round(b * 255)
    ];
}

function pixelToHex(pixel) {
    var hex = pixel.map(x=>{
        var normAmount = (x + 256) % 256;
        
        var s = Math.round(normAmount).toString(16);
        
        if(s.length < 2) return "0" + s;
        else return s.substring(0,2);
    }).join("");

    return "#" + hex;
}