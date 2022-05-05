var strategies = [
    pixelsToMatrix,
    geometryGrapher((x,y) => x + y),
    geometryGrapher((x,y) => y - x),
    geometryGrapher((x,y) => Math.max(y * x, y, x)),
    geometryGrapher((x,y) => x),
    geometryGrapher((x,y) => y),
    geometryGrapher((x,y) => Math.sqrt(x*x + y*y)),
    geometryGrapher((x,y,width) => width * Math.sin(x)),
    geometryGrapher((x,y) => y | x),
    geometryGrapher((x,y) => y & x),
    geometryGrapher((x,y) => y ^ x),
    geometryGrapher((x,y) => y % x),
    geometryGrapher((x,y,width) => (y - width/2) % (x - width/2)),
    geometryGrapher((x,y) => x % y),
    geometryGrapher((x,y,width) => (Math.atan2(x + width/2, y + width/2) / (Math.PI)) * width ),
    geometryGrapher((x,y,width) => (Math.atan2(x - width/2, y - width/2) / Math.PI) * width )
]

module.exports = randomStrategy;

function randomStrategy(pixels, strategySeed) {

    if(pixels.length == 0) return [];
    
    var matrix = selectRandomStrategy(strategySeed, pixels)(pixels);
    
    //santiy check to make sure it's always a matrix, even when there's no delta
    if(!matrix) return [];
    else return matrix;
}

function selectRandomStrategy(seed, pixels) {
    var index = seededRandom(pixels[0][1])() * strategies.length;
    var seedIndex = Math.floor(index % strategies.length)

    return strategies[seedIndex];
}

function geometryGrapher(geoCb) {    
    return function (pixels) {
        var seed = pixels[0][0];
        var random = seededRandom(seed);
        
        var supersample = 1 + Math.floor(Math.cbrt(random()) * 5);
    
        var possibleWidths = [x=>x, x=>x, x=>x, x=>supersample*x, x=>supersample*x, x=>512];
        var selectedWidthKey = possibleWidths[Math.floor(random() * possibleWidths.length)];
        
        var width = selectedWidthKey(pixels.length);
        var height = width;

        var result = [];
        
        for(var i = 0; i < height; i++) {
            var row = [];
            for(var j = 0; j < width; j++) {
                var scaledX = (j/width) * pixels.length * supersample;
                var scaledY = (i/height) * pixels.length * supersample;

                var pixIndex = geoCb(scaledX, scaledY, pixels.length * supersample) / supersample;

                if(isNaN(pixIndex)) pixIndex = 0;
                if(pixIndex == Infinity) pixIndex = pixels.length - 1;

                var absPixIndex = Math.abs(pixIndex) % pixels.length;

                var topPixel = pixels[Math.ceil(absPixIndex)] || pixels[pixels.length - 1];
                var bottomPixel = pixels[Math.floor(absPixIndex)];

                var pixelProportion = 1 - (absPixIndex - Math.floor(absPixIndex));
                
                row.push(weightedAvgPixel(topPixel, bottomPixel, pixelProportion));
            }
            result.push(row);
        }
        return result;
    }
}

function weightedAvgPixel(pixel1, pixel2, proportion) {
    if(!pixel1 && !pixel2) throw "Pixels are bad";

    if(!pixel1) return pixel2;
    if(!pixel2) return pixel1;

    return [
        pixel1[0] + (pixel2[0] - pixel1[0]) * proportion,
        pixel1[1] + (pixel2[1] - pixel1[1]) * proportion,
        pixel1[2] + (pixel2[2] - pixel1[2]) * proportion
    ]
}

function pixelsToMatrix(pixels) {
    var width = Math.ceil(Math.sqrt(pixels.length));
    
    var rows = [];
    for(var i = 0; i < pixels.length; i += width) {
        var row = pixels.slice(i, i + width);
        
        //fill shorter rows so the end isn't blank every time
        for(var j = i; row.length < width; j++) {
            row.push(pixels[j % pixels.length]);
        }
        
        rows.push(row);
    }
    return rows;
}

function seededRandom(seed) {
    return function mulberry32random() {
        var t = seed += 0x6D2B79F5;
        t = Math.imul(t ^ t >>> 15, t | 1);
        t ^= t + Math.imul(t ^ t >>> 7, t | 61);
        return ((t ^ t >>> 14) >>> 0) / 4294967296;
    }
}