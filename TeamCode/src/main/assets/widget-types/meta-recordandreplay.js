module.exports = {
    init: function (parent, state) {
        state.historyBuffer = [];

        var ICON_EJECT = "<svg width='1' height='1' viewBox='0 0 1 1'><path d='M 0,1 h 1 v -0.25 h -1 z M 0 0.5 L 0.5 0 L 1 0.5 Z'></svg>";
        var ICON_RECORD = "<svg width='1' height='1' viewBox='0 0 1 1'><path d='M 1,0.5 A 0.5,0.5 0 0 1 0.5,1 0.5,0.5 0 0 1 0,0.5 0.5,0.5 0 0 1 0.5,0 0.5,0.5 0 0 1 1,0.5 Z'></svg>";
        var ICON_STOP_RECORD = "<svg width='1' height='1' viewBox='0 0 1 1'><path d='M 0,0 H 1 V 1 H -1 Z'></svg>";
        var ICON_PLAY = "<svg width='1' height='1' viewBox='0 0 1 1'><path d='M 0,0 L 1 0.5 L 0 1 Z'></svg>";
        var ICON_PAUSE = "<svg width='1' height='1' viewBox='0 0 1 1'><path d='M 0.0,0 h 0.35 V 1 h -0.35 z M 1 0 h -0.35 V 1 h 0.35 z'></svg>";
        var ICON_PAUSE = "<svg width='1' height='1' viewBox='0 0 1 1'><path d='M 0.0,0 h 0.35 V 1 h -0.35 z M 1 0 h -0.35 V 1 h 0.35 z'></svg>";
        var ICON_DL = "<svg width='1' height='1' viewBox='0 0 1 1'><path d='M 0.42257158,0 V 0.70668617 L 0.1419469,0.42606148 0.03360137,0.53440702 0.3908488,0.89165445 h 4.8803e-4 L 0.49919434,0.99999997 0.60753986,0.89165445 0.96478729,0.53440702 0.85644175,0.42606148 0.57581708,0.70668617 V 0 Z'></svg>";

        parent.classList.add("widget--record");
        var buttonContainer = document.createElement("nav");
        parent.appendChild(buttonContainer);

        var buttonRecord = document.createElement("button");
        buttonRecord.classList.add("recorder");
        buttonRecord.addEventListener("click", clickRecordButton);
        function clickRecordButton() {
            state.recording = ((state.recording || 0) + 1) % 3;

            if (state.recording == 0) {
                reportStopRecordPlayback();
                state.historyBuffer = [];
                if (state.dataScrubberPath) state.dataScrubberPath.setAttribute("d", "");
            }

            if (state.recording == 2) {
                reportStartRecordPlayback();
                state.currentFrame = 1;
                scrubTo(0);
                dataScrubber.setAttribute("aria-valuemin", 0);
                dataScrubber.setAttribute("aria-valuemax", state.historyBuffer.length - 1);
                dataScrubber.setAttribute("step", 1);
            }

            updateIcons();
        }
        buttonContainer.appendChild(buttonRecord);

        var buttonPlaypause = document.createElement("button");
        buttonPlaypause.classList.add("play-pause");
        buttonPlaypause.addEventListener("click", clickPlaypauseButton);
        function clickPlaypauseButton() {
            if (state.recording == 2) {
                state.playing = !state.playing;
                if (state.playing) {
                    if (state.currentFrame === undefined) scrubTo(0);

                    var dsow = dataScrubber.offsetWidth;

                    function playNextFrame() {
                        if (!state.playing) return;

                        //if it's ended, loop
                        if (state.currentFrame + 1 == state.historyBuffer.length) {
                            resetWidgets();
                            state.currentFrame = 0;
                        }

                        var currentTime = state.historyBuffer[state.currentFrame].time;
                        var nextTime = state.historyBuffer[state.currentFrame + 1].time;
                        var tDelta = Math.abs(nextTime - currentTime);

                        dispatchData(state.historyBuffer[state.currentFrame]);
                        state.currentFrame++;

                        var scrubberPx = ((state.currentFrame + (state.historyBuffer.length % 2) / 2) / state.historyBuffer.length) * dsow;
                        dataScrubberCurrentTimeIndicator.style.transform = `translateX(${scrubberPx}px)`;

                        dataScrubber.setAttribute("aria-valuenow", state.currentFrame);

                        setTimeout(playNextFrame, tDelta);
                    }
                    playNextFrame();
                }
            }
            updateIcons();
        }
        buttonContainer.appendChild(buttonPlaypause);

        var buttonDownload = document.createElement("button");
        buttonDownload.classList.add("download");
        buttonContainer.appendChild(buttonDownload);
        buttonDownload.innerHTML = ICON_DL;
        buttonDownload.addEventListener("click", clickDownloadButton);
        function clickDownloadButton() {
            var flattened = state.historyBuffer.map(x => flattenObj(x));

            var keys = [];
            for (var i = 0; i < flattened.length; i++) {
                for (var key in flattened[i]) {
                    if (!keys.includes(key)) keys.push(key);
                }
            }

            var csvHeader = keys.map(x => csvEscape(x)).join(",");
            var rows = [csvHeader];
            for (var i = 0; i < flattened.length; i++) {
                var row = [];
                for (var j = 0; j < keys.length; j++) {
                    if (flattened[i][keys[j]] !== undefined) row.push(csvEscape(flattened[i][keys[j]]));
                    else row.push("");
                }
                rows.push(row.join(","));
            }

            var csvFile = rows.join("\n");
            var csvBlob = new Blob([csvFile], {type: "text/csv"})
            downloadURL(window.URL.createObjectURL(csvBlob), "recording-" + new Date().toISOString() + ".csv");
        }

        function updateIcons() {
            buttonRecord.setAttribute("red-icon", state.recording == 1);
            buttonPlaypause.setAttribute("greyed", state.recording != 2);
            buttonDownload.setAttribute("greyed", state.recording != 2);

            if (state.recording == 2) buttonRecord.innerHTML = ICON_EJECT;
            else buttonRecord.innerHTML = ICON_RECORD;

            if (state.playing) buttonPlaypause.innerHTML = ICON_PAUSE;
            else buttonPlaypause.innerHTML = ICON_PLAY;

            if (state.recording != 2) {
                dataScrubberHoverCursor.style.opacity = 0;
                dataScrubberCurrentTimeIndicator.style.opacity = 0;
            } else {
                dataScrubberHoverCursor.style.opacity = 0.4;
                dataScrubberCurrentTimeIndicator.style.opacity = 1;
            }
        }


        var dataScrubber = document.createElement("div");
        dataScrubber.setAttribute("role", "slider");
        dataScrubber.setAttribute("tabindex", "0");

        var dataScrubberPointsSvg = document.createElementNS("http://www.w3.org/2000/svg", "svg");
        dataScrubberPointsSvg.style.pointerEvents = "none";
        dataScrubberPointsSvg.style.width = "100%";
        dataScrubberPointsSvg.style.height = "100%";

        var dataScrubberPath = document.createElementNS("http://www.w3.org/2000/svg", "path");
        dataScrubberPath.style.fill = "var(--graph-label)";
        dataScrubberPointsSvg.appendChild(dataScrubberPath);
        state.dataScrubberPath = dataScrubberPath;

        var dataScrubberHoverCursor = document.createElementNS("http://www.w3.org/2000/svg", "path");
        dataScrubberHoverCursor.style.fill = "var(--widget-text-color)";
        dataScrubberPointsSvg.appendChild(dataScrubberHoverCursor);
        dataScrubberHoverCursor.setAttribute("d", "M0 0V1000h-1V0Z");
        dataScrubberHoverCursor.style.opacity = "0";
        dataScrubber.addEventListener("mousemove", function (e) {
            if (state.recording == 2) {
                var rawX = e.clientX - dataScrubber.offsetLeft;
                var normX = rawX / dataScrubber.offsetWidth;
                var roundX = Math.round(normX * state.historyBuffer.length) + (state.historyBuffer.length % 2) / 2;
                var finalX = (roundX / state.historyBuffer.length) * dataScrubber.offsetWidth;
                dataScrubberHoverCursor.style.transform = `translateX(${finalX}px)`;
            }
        });
        dataScrubber.addEventListener("mouseenter", function() {
            dataScrubberHoverCursor.style.opacity = 0.4;
        });
        dataScrubber.addEventListener("mouseleave", function() {
            dataScrubberHoverCursor.style.opacity = 0;
        });

        var dataScrubberCurrentTimeIndicator = document.createElementNS("http://www.w3.org/2000/svg", "path");
        dataScrubberCurrentTimeIndicator.style.fill = "var(--data-red)";
        dataScrubberPointsSvg.appendChild(dataScrubberCurrentTimeIndicator);
        dataScrubberCurrentTimeIndicator.setAttribute("d", "M0 0V1000h-1V0Z");
        dataScrubber.addEventListener("click", function (e) {
            if (state.recording == 2) {
                var rawX = e.clientX - dataScrubber.offsetLeft;
                var normX = rawX / dataScrubber.offsetWidth;
                var roundX = Math.round(normX * state.historyBuffer.length);
                scrubTo(roundX);
            }
        });
        dataScrubber.addEventListener("keydown", function (e) {
            if (state.recording == 2) {
                console.log(e.key);
                
                if (e.key == "ArrowRight") {
                    scrubTo(state.currentFrame + 1);
                } else if (e.key == "ArrowLeft") {
                    scrubTo(state.currentFrame - 1);
                } else if(!isNaN(+e.key)) {
                    var parseNum = +e.key;
                    console.log("pNum", parseNum);
                    var frame = Math.round((parseNum / 10) * state.historyBuffer.length);
                    console.log(frame);
                    scrubTo(frame);
                }
            }
        });


        function scrubTo(frame) {
            var oldFrame = state.currentFrame;
            
            frame = (frame + state.historyBuffer.length) % state.historyBuffer.length;
            state.currentFrame = frame;


            var scrubberPx = ((frame + (state.historyBuffer.length % 2) / 2) / state.historyBuffer.length) * dataScrubber.offsetWidth;
            dataScrubberCurrentTimeIndicator.style.transform = `translateX(${scrubberPx}px)`;

            if(frame > oldFrame) {
                for (var i = oldFrame; i <= frame; i++) {
                    dispatchData(state.historyBuffer[i]);
                }
            } else if(frame < oldFrame) {   
                resetWidgets();
                for (var i = 0; i <= frame; i++) {
                    dispatchData(state.historyBuffer[i]);
                }
            }
            
            dataScrubber.setAttribute("aria-valuenow", frame);
        }

        dataScrubber.appendChild(dataScrubberPointsSvg);
        parent.appendChild(dataScrubber);
        updateIcons();
        
        parent.setAttribute("tabIndex", 0);
        parent.addEventListener("keydown", function(e) {
            if(e.key == " " || e.key == "Enter") e.preventDefault();
            
            if(e.key == " ") {
                if(state.recording == 2) clickPlaypauseButton();
                else clickRecordButton();
            } else if(e.key == "Delete") {
                if(state.recording == 2) {
                    clickRecordButton();
                }
            } else if(e.key == "D") {
                clickDownloadButton();
            } else {
                var cloneEvent = new KeyboardEvent("keydown", {
                    key: e.key,
                    keyCode: e.keyCode,
                    location: e.location,
                    code: e.code,
                    ctrlKey: e.ctrlKey,
                    shiftKey: e.shiftKey,
                    altKey: e.altKey,
                    metaKey: e.metaKey,
                    repeat: e.repeat,
                    isComposing: e.isComposing,
                    charCode: e.charCode,
                    which: e.which
                });
                dataScrubber.dispatchEvent(cloneEvent);
            }
        })

    },
    reset: function (parent, state) {
        if (state.recording != 2) {
            state.historyBuffer = [];
            if (state.dataScrubberPath) state.dataScrubberPath.setAttribute("d", "");
        }
    },
    onbeforedelete: function () {
        reportStopRecordPlayback();
        resetWidgets();
    },
    ondata: function (data, parent, state, config) {
        if (state.recording == 1) {
            state.historyBuffer.push(data);

            if (!state.dataScrubberTotalWidth) state.dataScrubberTotalWidth = state.dataScrubberPath.parentElement.clientWidth;
            var dataScrubberTotalWidth = state.dataScrubberTotalWidth;

            var dataScrubberTotalHeight = state.dataScrubberTotalHeight;
            if (!state.dataScrubberTotalHeight) {
                var h = state.dataScrubberPath.parentElement.clientHeight;
                if (!state.smallDataScrubberHeight) state.smallDataScrubberHeight = h;
                if (h > state.smallDataScrubberHeight) state.dataScrubberTotalHeight = h;

                dataScrubberTotalHeight = h;
            }


            var dataScrubberPathCode = "";

            var renderedPathPoints = Math.min(Math.floor(state.dataScrubberTotalWidth / 4), state.historyBuffer.length);
            renderedPathPoints += state.historyBuffer.length % 4;
            for (var i = 0; i < renderedPathPoints; i++) {
                var x = i + (state.historyBuffer.length % 2) / 2;
                dataScrubberPathCode += "M" + (dataScrubberTotalWidth * (x / renderedPathPoints)) + " " + 1000;

                var barHeight = -1000;

                dataScrubberPathCode += `v ${barHeight} h 1 v ${barHeight} z`;
            }

            state.dataScrubberPath.setAttribute("d", dataScrubberPathCode);
        }
    }
}