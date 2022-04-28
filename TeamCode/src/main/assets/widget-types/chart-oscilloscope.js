
var WIDGET_PADDING = 5;

var evaluateFormula = require("../helper-scripts/evaluate-formula.js");

var canvas, ctx, width, height, cssVariables;

module.exports = {
    init: function (parent, state, config, box) {
        //use object.assign to avoid mutation of original box
        state.box = {};
        Object.assign(state.box, box);

        state.datapoints = [];

        canvas = document.createElement("canvas");
        canvas.style.position = "absolute";

        width = canvas.width = box.width * gridSize - WIDGET_PADDING * 2;
        height = canvas.height = box.height * gridSize - WIDGET_PADDING * 2;

        ctx = canvas.getContext("2d");

        cssVariables = getComputedStyle(canvas);

        parent.appendChild(makeBackgroundCanvas());

        parent.appendChild(canvas);
        parent.classList.add("nopadding");
        parent.classList.add("nooverflow");
    },
    reset: function (parent, state) {
        state.datapoints = [];
    },
    ondata: function (data, parent, state, config) {

        var dataGreenColor = cssVariables.getPropertyValue("--data-green");

        if(!config.xAxis || !config.yAxis) return console.log(config);
        var xCoord = data.fields[config.xAxis];
        var yCoord = data.fields[config.yAxis];

        var xScaleMin = 0;
        var xScaleMax = 0;
        var yScaleMin = 0;
        var yScaleMax = 0;


        var formulaArgs = {
            values: [xCoord, yCoord],
            data: state.datapoints
        }

        xCoord = evaluateFormula(config.xFormula, formulaArgs);
        yCoord = evaluateFormula(config.yFormula, formulaArgs);


        xScaleMin = evaluateFormula(config.xScaleMin, formulaArgs);
        xScaleMax = evaluateFormula(config.xScaleMax, formulaArgs);
        yScaleMin = evaluateFormula(config.yScaleMin, formulaArgs);
        yScaleMax = evaluateFormula(config.yScaleMax, formulaArgs);

        state.datapoints.push({
            x: xCoord,
            y: yCoord,
            time: data.time
        });


        //remove old datapoints
        var lastValidTime = data.time - config.dataStorageLengthSeconds * 1000;
        var firstValidIndex = -1;
        for(var i = 0; i < state.datapoints.length; i++) {
            if(state.datapoints[i].time > lastValidTime) {
                firstValidIndex = i;
                break;
            }
        }
        //erase old canvas
        ctx.clearRect(0, 0, width, height);

        //don't perform slicing if all points are valid
        if(firstValidIndex > 0) {
            state.datapoints = state.datapoints.slice(firstValidIndex);
        }

        ctx.strokeStyle = dataGreenColor;
        ctx.fillStyle = dataGreenColor;
        ctx.lineWidth = 2;


        //if there aren't any points, don't bother drawing them
        if(state.datapoints.length == 0) return;

        //draw the new points
        drawPath(state.datapoints, xScaleMin, xScaleMax, yScaleMin, yScaleMax);
    },
    config: [
        {
            name: "xAxis",
            type: "select",
            value: "field"
        },
        {
            name: "xScaleMin",
            type: "text",
            default: -1
        },
        {
            name: "xScaleMax",
            type: "text",
            default: 1
        },
        {
            name: "xFormula",
            type: "text",
            default: "x"
        },
        {
            name: "yAxis",
            type: "select",
            value: "field"
        },
        {
            name: "yScaleMin",
            type: "text",
            default: -1
        },
        {
            name: "yScaleMax",
            type: "text",
            default: 1
        },
        {
            name: "yFormula",
            type: "text",
            default: "y"
        },
        {
            name: "dataStorageLengthSeconds",
            type: "number",
            default: 2
        }
    ]
}

function drawPath(points, minX, maxX, minY, maxY) {
    ctx.beginPath();

    for(var i = 0; i < points.length; i++) {
        var scaled = scalePoint(points[i], minX, maxX, minY, maxY);
        
        var x = Math.floor(scaled.x * width);
        var y = height - Math.floor(scaled.y * height);

        if(i == 0) ctx.moveTo(x, y);
        else ctx.lineTo(x, y);
    }

    ctx.stroke();

    //make a circle at the latest point
    ctx.clearRect(x - 4, y - 4, 8, 8); //clear the area around the circle so it's prominent
    ctx.beginPath();
    ctx.ellipse(x, y, 3, 3, 0, 0, Math.PI * 2);
    ctx.fill();
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

function makeBackgroundCanvas() {
    var bgCanvas = document.createElement("canvas");
    bgCanvas.style.position = "absolute";
    bgCanvas.width = width;
    bgCanvas.height = height;

    var ctx = bgCanvas.getContext("2d");

    ctx.strokeStyle = cssVariables.getPropertyValue("--graph-line");
    ctx.globalAlpha = 0.5;

    var lineIncrementDecimal = 0.25;

    //horizontal lines
    for(var i = 0.25; i <= 1; i += lineIncrementDecimal) {
        ctx.beginPath();
        ctx.moveTo(0, height * i);
        ctx.lineTo(width, height * i);
        ctx.stroke();
    }

    //vertical lines
    for(var i = 0.25; i <= 1; i += lineIncrementDecimal) {
        ctx.beginPath();
        ctx.moveTo(width * i, 0);
        ctx.lineTo(width * i, height);
        ctx.stroke();
    }


    //clear cross-points
    var crossPointClearSize = Math.floor(Math.min(width, height) / 32);

    for(var x = 0; x <= 1; x += lineIncrementDecimal) {
        for(var y = 0; y <= 1; y += lineIncrementDecimal) {
            ctx.clearRect(width * x - crossPointClearSize, height * y - crossPointClearSize, crossPointClearSize * 2, crossPointClearSize * 2);
        }
    }

    return bgCanvas;
}