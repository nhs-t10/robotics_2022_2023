module.exports = {
    init: function(parent, state, config, box) {
        //use object.assign to avoid mutation of original box
        state.box = {};
        Object.assign(state.box, box);

        state.datapoints = [];
        state.barCount = 0;

        var svg = document.createElementNS("http://www.w3.org/2000/svg", "svg");
        svg.classList.add("widget--line-chart");
        
        //scale up for a crisper view
        state.box.height *= 2 * gridSize;
        state.box.width *= 2 * gridSize;

        svg.setAttribute("height", state.box.height);
        svg.setAttribute("width", state.box.width);

        svg.setAttribute("viewBox", `0 0 ${state.box.width} ${state.box.height}`);
        svg.setAttribute("preserveAspectRatio", "xMidYMid meet");
        svg.setAttribute("xmlns", "http://www.w3.org/2000/svg");

        //caption lines
        for (var i = 0; i <= 4; i++) {
            var captionLine = document.createElementNS("http://www.w3.org/2000/svg", "path");
            captionLine.classList.add("widget--line-chart--caption-line");
            captionLine.setAttribute("d", "M0," + (state.box.height * i / 4) + "H" + state.box.width);
            svg.appendChild(captionLine);
        }
        
        var barGroup = document.createElementNS("http://www.w3.org/2000/svg", "g");
        barGroup.classList.add("widget--line-chart--fill-line");
        svg.appendChild(barGroup);

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
        
        state.svg = barGroup;

        parent.appendChild(svg);
        parent.classList.add("nopadding");
        parent.classList.add("nooverflow");
    },
    reset: function(parent, state) {
        
    },
    ondata: function(data, parent, state, config) {
        if(state.bars == undefined) state.bars = {};
        
        var fieldsToShow = config.fields || [];
        
        
        
        var maxFieldNumber = config.yScaleMax || data.fields[fieldsToShow[0]] || 0;
        var minFieldNumber = config.yScaleMin || data.fields[fieldsToShow[0]] || 0;
        
        for(var i = 0; i < fieldsToShow.length; i++) {
            if(state.bars[fieldsToShow[i]] == undefined) state.bars[fieldsToShow[i]] = makeBar(fieldsToShow[i]);
            
            if(!config.maxValueScale) maxFieldNumber = Math.max(maxFieldNumber, data.fields[fieldsToShow[i]]);
            if(!config.minValueScale) minFieldNumber = Math.min(minFieldNumber, data.fields[fieldsToShow[i]]);
        }
        
        updateCaptions(maxFieldNumber, minFieldNumber);
        
        for(var i = 0; i < fieldsToShow.length; i++) {
            var range = maxFieldNumber - minFieldNumber;
            var number = data.fields[fieldsToShow[i]];
            var normalized = (number - minFieldNumber) / range;
            
            state.bars[fieldsToShow[i]].setIndex(i);
            state.bars[fieldsToShow[i]].update(normalized);
        }
        
        var barNames = Object.keys(state.bars);
        for(var i = 0; i < barNames.length; i++) {
            if(!fieldsToShow.includes(barNames[i])) {
                state.bars[barNames[i]].destroy();
                delete state.bars[barNames[i]];
            }
        }
        
        
        
        function updateCaptions(max, min) {
            state.captions.forEach(x=>x(max, min))
        }
        
        function makeBar() {
            var barRect = document.createElementNS("http://www.w3.org/2000/svg", "rect");
            
            state.svg.appendChild(barRect);
            
            var selfBarIndex = state.barCount;
            state.barCount++;
            
            return {
                setIndex: function(i) {
                    selfBarIndex = i;
                },
                destroy: function() {
                    state.barCount--;
                    if(barRect.parentElement) barRect.parentElement.removeChild(barRect);
                },
                update: function(normalizedNumber) {
                    var totalMarginWidth = state.box.width  * 0.1;
                    var marginWidth = totalMarginWidth / (state.barCount - 1);
                    var width = (state.box.width - totalMarginWidth) / state.barCount;
                    
                    barRect.setAttribute("width", width);
                    barRect.setAttribute("x", width * selfBarIndex + (selfBarIndex + 0.5) * marginWidth);
                    
                    barRect.setAttribute("height", normalizedNumber * state.box.height);
                    barRect.setAttribute("y", (1 - normalizedNumber) * state.box.height);
                }
            }
        }
    }, 
    config: [
        {
            name: "fields",
            type: "select",
            value: "field[]"
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
        }
    ]
}