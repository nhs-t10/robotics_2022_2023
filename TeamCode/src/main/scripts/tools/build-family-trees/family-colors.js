var crypto = require("crypto");

var familyRecords = {};

var PHI = 1.61803399;

/**
 * @typedef {object} Heraldry
 * @property {string} primary
 * @property {string} secondary
 * @property {string} primaryDark
 * @property {string} primaryLight
 * @property {string} primaryVDark
 * @property {string} primaryPastel
 * @property {*} crestCircled
 */

module.exports = {
    primary: primary,
    fullColorScheme: fullColorScheme
}

/**
 * 
 * @param {*} build 
 * @returns {string}
 */
function primary(build) {
    return fullColorScheme(build).primary;
}
/**
 * 
 * @param {*} build 
 * @returns {Heraldry}
 */
function fullColorScheme(build) {
    var cognomen = build.cognomen;
    if(familyRecords[cognomen]) return familyRecords[cognomen];

    var colours = (familyRecords[cognomen] = {});
    
    var familyHash = sha(cognomen);

    var prettyPixels = normalizePixels(getHexPixels(familyHash));

    colours.primary = pixelToHex(prettyPixels[0]);
    colours.secondary = pixelToHex(prettyPixels[1]);
    
    colours.primaryVDark = pixelToHex(shade(prettyPixels[0], 0.2));
    colours.primaryDark = pixelToHex(shade(prettyPixels[0], 0.5));
    colours.primaryLight = pixelToHex(shade(prettyPixels[0], 1.25));
    colours.primaryPastel = pixelToHex(shade(prettyPixels[0], 2.5));
    
    colours.crestCircled = function(x, y, size) {
        var rand = seededRandom(parseInt(familyHash.substring(0,8), 16));
        var ellipse = `<ellipse fill="${colours.primaryVDark}" rx="${size/2}" ry="${size/2}" cx="${x}" cy="${y}"/>`;
            
        var p = `<path fill="${colours.primary}" stroke-width="2" stroke="${colours.primary}" d="M ${x} ${y}`
        for(var i = 0; i < 6; i++) {
            var angle = rand() * Math.PI * 2;
            var mag = rand() * size / 2;
            
            p += `L${x + Math.cos(angle) * mag} ${y + Math.sin(angle) * mag}`;
        }
        p += `"/>`;
        
        return ellipse + p;
    }

    return colours;
}

function sha(t) {
    return crypto.createHash("sha256").update(t + "").digest("hex");
}

function shade(color, coef) {
    var hsv = RGBtoHSV(color[0], color[1], color[2]);
    
    var rgbO = HSVtoRGB(hsv[0], hsv[1], hsv[2] * coef);
    return [rgbO.r, rgbO.g, rgbO.b];
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
        
    scaledPixels = scaledPixels.sort((a,b) => a[1] - b[1]);
    
    var rgbScaled = scaledPixels.map(x=>HSVtoRGB(x[0], x[1], x[2]));
    
    var rgbScaledArrs = rgbScaled.map(x=>[x.r, x.g, x.b]);
    return rgbScaledArrs;
}

function nextPhiHue(currentHue, lastPixel1, lastPixel2) {
    if(!lastPixel1 || !lastPixel2) return currentHue;
    
    var lastHue1 = lastPixel1[0], lastHue2 = lastPixel2[0];
    
    var hueDifference = lastHue2 - lastHue1;
    var hueOffset = hueDifference * PHI;
    
    var nextHue = lastHue1 + hueOffset;
    
    return (nextHue + 2) % 1;
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
function pixelToHex(pixel) {
    var hex = pixel.map(x=>{
        var normAmount = (x + 256) % 256;
        
        var s = Math.round(normAmount).toString(16);
        
        if(s.length < 2) return "0" + s;
        else return s.substring(0,2);
    }).join("");

    return "#" + hex;
}

function seededRandom(seed) {
    return function mulberry32random() {
        var t = seed += 0x6D2B79F5;
        t = Math.imul(t ^ t >>> 15, t | 1);
        t ^= t + Math.imul(t ^ t >>> 7, t | 61);
        return ((t ^ t >>> 14) >>> 0) / 4294967296;
    }
}