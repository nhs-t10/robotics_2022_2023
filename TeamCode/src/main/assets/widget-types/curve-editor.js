
var WIDGET_PADDING = 5;

var evaluateFormula = require("../helper-scripts/evaluate-formula.js");

var canvas, ctx, width, height, cssVariables, pathChanged;

var scaleStartX = 0, scaleStartY = 0, scaleEndX = 1, scaleEndY = 1;

module.exports = {
    init: function (parent, state, config, box) {
        //use object.assign to avoid mutation of original box
        state.box = {};
        Object.assign(state.box, box);

        state.datapoints = [];

        canvas = document.createElement("canvas");
        canvas.style.position = "absolute";

        width = canvas.width = box.width * gridSize - WIDGET_PADDING * 2;
        height = canvas.height = box.height * gridSize * 0.8 - WIDGET_PADDING * 2;

        //make scale uniform with canvas's size
        scaleEndX /= (width / height);

        ctx = canvas.getContext("2d");

        cssVariables = getComputedStyle(canvas);

        var bg = regenerateBackgroundCanvas();

        canvas.addEventListener("wheel", function(e) {
            var zoomCenterX = scale(e.offsetX, 0, width);
            var zoomCenterY = scale(height - e.offsetY, 0, height);

            var sizeX = scaleEndX - scaleStartX;

            var zoomAmount = sizeX * Math.sign(e.deltaY) / 2;

            scaleEndX += (1 - zoomCenterX) * zoomAmount;
            scaleStartX -= (zoomCenterX) * zoomAmount;

            scaleEndY += (1 - zoomCenterY) * zoomAmount;
            scaleStartY -= (zoomCenterY) * zoomAmount;

            regenerateBackgroundCanvas(bg);
            updatePath(canvas);
        });

        makeDraggy(canvas, bg);
    
        makePathEditable(canvas);

        pathChanged = function() {
            updatePath(canvas);
        }

        var coefInputs = makeCoefInputs();

        var canvases = document.createElement("div");
        canvases.style.position = "relative";
        canvases.appendChild(bg);
        canvases.appendChild(canvas);

        parent.appendChild(coefInputs);
        parent.appendChild(canvases);
        parent.classList.add("nopadding");
        parent.classList.add("nooverflow");
    },
    reset: function (parent, state) {
        state.datapoints = [];
    },
    ondata: function (data, parent, state, config) {
        
        var dataGreenColor = cssVariables.getPropertyValue("--data-green");
    },
    config: [
        {
            name: "curveName",
            type: "text"
        }
    ]
}

function scalePoint(point, minX, maxX, minY, maxY) {
    return {
        x: scale(point.x, minX, maxX),
        y: scale(point.y, minY, maxY)
    };
}

function scale(n, min, max) {
    return (n - min) / (max - min);
}

function regenerateBackgroundCanvas(bgCanvas) {
    if(bgCanvas === undefined) bgCanvas = document.createElement("canvas");

    bgCanvas.style.position = "absolute";
    bgCanvas.width = width;
    bgCanvas.height = height;

    /**
     * @type {CanvasRenderingContext2D}
     */
    var ctx = bgCanvas.getContext("2d");

    ctx.strokeStyle = cssVariables.getPropertyValue("--graph-line");
    ctx.fillStyle = cssVariables.getPropertyValue("--graph-line");
    ctx.globalAlpha = 0.5;

    var sizeX = scaleEndX - scaleStartX;
    var sizeY = scaleEndY - scaleStartY;

    var centerX = scaleStartX + sizeX/2;
    var centerY = scaleStartY + sizeY/2;

    var exponentX = Math.pow(10, Math.floor(Math.max(Math.log10(sizeX), Math.log10(sizeY))) - 0.25);
    var exponentY = exponentX;

    var lineIncrementDecimalX = Math.abs(exponentX);
    var lineIncrementDecimalY = Math.abs(exponentY);


    //horizontal lines, measuring the Y scale
    var lineStartY = Math.floor(scaleStartY / lineIncrementDecimalY) * lineIncrementDecimalY;

    for(var i = lineStartY; i <= scaleEndY; i += lineIncrementDecimalY) {
        if(i % (10 * lineIncrementDecimalX) == 0) ctx.lineWidth = 3;
        else ctx.lineWidth = 1;
        
        var p = scale(i, scaleStartY, scaleEndY);

        ctx.beginPath();
        ctx.moveTo(0, height - height * p);
        ctx.lineTo(width, height - height * p);
        ctx.stroke();
    }

    //vertical lines
    var lineStartX = Math.floor(scaleStartX / lineIncrementDecimalX) * lineIncrementDecimalX;

    for(var i = lineStartX; i <= scaleEndX; i += lineIncrementDecimalX) {
        if(i % (10 * lineIncrementDecimalX) == 0) ctx.lineWidth = 3;
        else ctx.lineWidth = 1;

        var p = scale(i, scaleStartX, scaleEndX);

        ctx.beginPath();
        ctx.moveTo(width * p, 0);
        ctx.lineTo(width * p, height);
        ctx.stroke();
    }


    //add labels!
    ctx.text
    ctx.textAlign = "center";
    
    ctx.textBaseline = "top";
    ctx.fillText(scaleEndY.toPrecision(5), width / 2, 0);

    ctx.textBaseline = "bottom";
    ctx.fillText(scaleStartY.toPrecision(5) + "", width / 2, height);

    ctx.textBaseline = "middle";

    ctx.textAlign = "left";
    ctx.fillText(scaleStartX.toPrecision(5), 0, height / 2);

    ctx.textAlign = "right";
    ctx.fillText(scaleEndX.toPrecision(5), width, height / 2);

    return bgCanvas;
}

/**
 * 
 * @param {HTMLElement} canvas 
 */
function makeDraggy(canvas, bgCanvas) {
    var mousedown, mouseStartX, mouseStartY
    canvas.addEventListener("mousedown", function(e) {
        e.stopPropagation();
        mousedown = true;
        mouseStartX = e.clientX;
        mouseStartY = e.clientY;
    });
    canvas.addEventListener("mouseup", function(e) {
        e.stopPropagation();
        mousedown = false;
    });
    canvas.addEventListener("mouseleave", function(e) {
        e.stopPropagation();
        mousedown = false;
    });
    canvas.addEventListener("mousemove", function(e) {
        e.stopPropagation();
        if(mousedown) {
            var sizeX = scaleEndX - scaleStartX, sizeY = scaleEndY - scaleStartY;
            
            var mouseDeltaSizeX = ((e.clientX - mouseStartX) / width) * sizeX;
            var mouseDeltaSizeY = ((e.clientY - mouseStartY) / height) * sizeY;


            scaleStartX -= mouseDeltaSizeX;
            scaleEndX -= mouseDeltaSizeX;

            scaleStartY += mouseDeltaSizeY;
            scaleEndY += mouseDeltaSizeY;

            mouseStartX = e.clientX;
            mouseStartY = e.clientY;

            regenerateBackgroundCanvas(bgCanvas);
            updatePath(canvas);
        }
    })
}

var pathCoefs = [], pathDegree = 0;

function makePathEditable(canvas) {
    updatePath(canvas);
}

function updatePath(canvas) {

    /**
     * @type {CanvasRenderingContext2D}
     */
    var ctx = canvas.getContext("2d");
    
    ctx.strokeStyle = cssVariables.getPropertyValue("--data-green");
    ctx.lineWidth = 2;

    var unitsPerPixelX = Math.abs((scaleEndX - scaleStartX) / width);

    ctx.clearRect(0, 0, width, height);
    
    ctx.beginPath();

    var firstPoint = scaleSvgPoint([scaleStartX,polyYCoord(scaleStartX)]);
    ctx.moveTo(firstPoint[0], height - firstPoint[1]);

    for(var i = 0; i < width; i++) {
        var x = scaleStartX + i * unitsPerPixelX;
        var y = polyYCoord(x);
        var canvasCoords = scaleSvgPoint([x,y]);
        ctx.lineTo(canvasCoords[0], height - canvasCoords[1]);
    }

    ctx.stroke();
}

function polyYCoord(x) {
    var s = 0;
    for(var i = 0; i < pathCoefs.length; i++) s += Math.pow(x, pathDegree - i) * pathCoefs[i];
    return s;
}

function scaleSvgPoint(p) {
    return [
        scale(p[0], scaleStartX, scaleEndX) * width,
        scale(p[1], scaleStartY, scaleEndY) * height,
    ];
}

function makeCoefInputs() {
    pathCoefs = [1,1,1,1,1,1,1];
    pathDegree = 3;

    var parent = document.createElement("div");
    parent.style.height = (height * 0.25) + "px";

    for(var i = pathDegree; i >= -pathDegree; i--) {
        parent.appendChild(makeCoefControl(i));
        //add a + if this isn't the last one
        if(i > -pathDegree) parent.appendChild(document.createTextNode("+"))
    }

    return parent;
}

function makeCoefControl(power) {
    var p = document.createElement("span");

    var index = pathDegree - power;

    var input = document.createElement("input");
    input.type = "number";
    
    input.style.color = "inherit";
    input.style.width = "3em";
    input.style.background = "var(--widget-background-color)";

    input.value = pathCoefs[index] || 0;
    input.addEventListener("change", function() {
        while(pathCoefs.length <= index) pathCoefs.push(0);
        pathCoefs[index] = input.valueAsNumber;
        pathChanged(); 
    });

    p.appendChild(input);

    var explanationText = "x^" + power;
    if(power == 0) explanationText = "";
    if(power == 1) explanationText = "x";
    p.appendChild(document.createTextNode(explanationText));

    return p;
}