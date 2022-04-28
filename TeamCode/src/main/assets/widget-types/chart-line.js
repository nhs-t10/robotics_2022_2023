var evaluateFormula = require("../helper-scripts/evaluate-formula.js");

module.exports = {
    init: function (parent, state, config, box) {
        //use object.assign to avoid mutation of original box
        state.box = {};
        Object.assign(state.box, box);

        state.datapoints = [];

        var svg = document.createElementNS("http://www.w3.org/2000/svg", "svg");
        svg.classList.add("widget--line-chart");

        svg.setAttribute("height", box.height);
        svg.setAttribute("width", box.width);

        //scale up for a crisper view
        state.box.height *= 2 * gridSize;
        state.box.width *= 2 * gridSize;

        svg.setAttribute("viewBox", `0 0 ${state.box.width} ${state.box.height}`);
        svg.setAttribute("preserveAspectRatio", "xMidYMid meet");
        svg.setAttribute("xmlns", "http://www.w3.org/2000/svg");

        var path = document.createElementNS("http://www.w3.org/2000/svg", "path");
        path.classList.add("widget--line-chart--stroke-line");

        var fillPath = document.createElementNS("http://www.w3.org/2000/svg", "path");
        fillPath.classList.add("widget--line-chart--fill-line");

        //caption lines
        for (var i = 0; i <= 4; i++) {
            var captionLine = document.createElementNS("http://www.w3.org/2000/svg", "path");
            captionLine.classList.add("widget--line-chart--caption-line");
            captionLine.setAttribute("d", "M0," + (state.box.height * i / 4) + "H" + state.box.width);
            svg.appendChild(captionLine);
        }

        state.captions = [];
        //captions themselves. They go after so that they'll overlay the lines.
        for (var i = 0; i <= 4; i++) {
            //use IEFE so that `i` is preserved in each
            (function (i) {
                var caption = document.createElementNS("http://www.w3.org/2000/svg", "text");
                caption.classList.add("widget--line-chart--caption");
                caption.setAttribute("x", 0);
                caption.setAttribute("y", (state.box.height * i / 4) - 6);


                state.captions.push(function (max, min) {
                    var range = max - min;
                    caption.textContent = (min + (range * (1 - i / 4))).toString().substring(0, 5);
                });

                svg.appendChild(caption);
            })(i);
        }

        svg.appendChild(fillPath);
        svg.appendChild(path);


        state.path = path;
        state.fill = fillPath;

        parent.appendChild(svg);
        parent.classList.add("nopadding");
        parent.classList.add("nooverflow");
    },
    reset: function (parent, state) {
        state.datapoints = [];
        if (state.path) state.path.setAttribute("d", "");
        if (state.fill) state.fill.setAttribute("d", "");
    },
    ondata: function (data, parent, state, config) {
        var num = data.fields[config.yAxis];

        try {
            num = evaluateFormula(config.formula, {
                data: state.datapoints,
                values: [num]
            });
        } catch(e) {}

        state.datapoints.push({
            x: data.time,
            y: num
        });

        var dataDisplayed = sliceData(state.datapoints, data.time - config.xScaleSeconds * 1000);

        var line = drawLine(dataDisplayed, state.box, config.xScaleSeconds * 1000);

        var max = dataDisplayed[0].y;
        var min = dataDisplayed[0].y;
        for (var i = 0; i < dataDisplayed.length; i++) {
            max = Math.max(max, dataDisplayed[i].y);
            min = Math.min(min, dataDisplayed[i].y);
        }

        max = Math.round(max * 1000) / 1000;
        min = Math.round(min * 1000) / 1000;

        min = config.yScaleMin || min;
        max = config.yScaleMax || max;

        for (var i = 0; i < state.captions.length; i++) {
            state.captions[i](max, min);
        }

        state.path.setAttribute("d", line);
        state.fill.setAttribute("d", line + "L" + state.box.width + "," + state.box.height + "L" + (0) + "," + (state.box.height) + "Z");
    },
    config: [
        {
            name: "yAxis",
            type: "select",
            value: "field"
        },
        {
            name: "xScaleSeconds",
            type: "number",
            default: 5
        },
        {
            name: "yScaleMin",
            type: "number",
            default: 0
        },
        {
            name: "yScaleMax",
            type: "number",
            default: 0
        },
        {
            name: "formula",
            type: "text",
            default: "x"
        }

    ]
}