var lastBuildRegisteredName, addBuildHistory;

module.exports = {
    init: function (parent, state, config, box) {
        var title = document.createElement("h2");
        title.setAttribute("style", 'margin:0;grid-area: 1/2/1/3;');
        var image = document.createElement("img");
        image.setAttribute("style", 'margin-right:0.75em;border-radius:2em;width:4em;height:4em;grid-area: 1/1/3/2;');
        var hash = document.createElement("span");
        hash.setAttribute("style",'grid-area: 2/2/2/3;');
        
        var toprow = document.createElement("div");
        toprow.setAttribute("style", 'display:grid;grid-template-columns:min-content;');

        toprow.appendChild(image);
        toprow.appendChild(title);
        toprow.appendChild(hash);

        parent.appendChild(toprow);
        
        addBuildHistory = function(buildHistory) {
            lastBuildRegisteredName = buildHistory.name;

            title.textContent = buildHistory.name;
            image.src = buildHistory.img;
            hash.textContent = buildHistory.hash.substring(0,9);
        }
        if(configGlobals.buildHistoryInfo) {
            addBuildHistory(configGlobals.buildHistoryInfo);
        } else {
            var xhr = new XMLHttpRequest();
            xhr.onload = function() {
                addBuildHistory(JSON.parse(xhr.responseText));
            }
            xhr.open("GET", "/build-history");
            xhr.send();
        }
    },
    reset: function () { },
    ondata: function (data, parent, state, config) {
        //if the hash has changed, re-update the display
        if(configGlobals.buildHistoryInfo){
            if(configGlobals.buildHistoryInfo.name != lastBuildRegisteredName) {
                addBuildHistory(configGlobals.buildHistoryInfo);
            }
        }
    },
    config: []
}