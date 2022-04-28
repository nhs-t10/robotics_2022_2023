module.exports = {
    init: function (parent, state, config, box) {

        var statepathlist = document.createElement("ul");
        statepathlist.classList.add("widget--autoauto-state-layout--list");
        parent.appendChild(statepathlist);
        state.list = statepathlist;
    },
    reset: function (parent, state) {
        if (state.list) {
            while (state.list.children[0]) {
                state.list.removeChild(state.list.children[0]);
            }
        }
        state.statepathListInitiated = false;
    },
    ondata: function (data, parent, state, config) {
        if (!state.statepathListInitiated) {
            if (!data.autoautoProgram) return false;

            state.statepaths = {};

            var programOutline = data.autoautoProgram;

            //it's a full program json wow
            if (programOutline.type === "Program") programOutline = programOutline.statepaths.map(x => [x.label.value, x.statepath.states.length]);
            else programOutline = Object.entries(programOutline);

            programOutline.forEach(x => {
                state.statepaths[x[0]] = [];

                var statepathListItem = document.createElement("li");
                var statepathNameHeader = document.createElement("h2");
                statepathNameHeader.textContent = x[0];

                var stateList = document.createElement("ol");

                statepathListItem.appendChild(statepathNameHeader);
                statepathListItem.appendChild(stateList);

                state.list.appendChild(statepathListItem);

                for (var i = 0; i < x[1]; i++) {
                    (function (i) {
                        var stateButtonLi = document.createElement("li");
                        var button = document.createElement("button");

                        state.statepaths[x[0]].push(stateButtonLi);

                        stateButtonLi.appendChild(button);
                        button.textContent = i;
                        button.addEventListener("click", function () {
                            sendCommand("auauss", configGlobals.streamID, x[0], i);
                        });

                        stateList.appendChild(stateButtonLi);
                    })(i);
                }
            });

            state.statepathListInitiated = true;
        }
        if (state.selectedButton) state.selectedButton.classList.remove("selected");
        if (state.statepaths[data.fields["--statepathName"]) {
            var statepathsButtons = state.statepaths[data.fields["--statepathName"]];

            if (statepathsButtons[+data.fields["--stateNumber"]]) {
                state.selectedButton = statepathsButtons[+data.fields["--stateNumber"]];
                state.selectedButton.classList.add("selected");
            }
        }
    }
}