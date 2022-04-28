module.exports = {
    init: function(parent, state, config, box) {
        var failTypeAcronyms = {
            "PFR": "POWER_FAILURE",
            "CFR": "CONTROL_FAILURE",
            "IMT": "INTERMITTENT_FAILURES",
            "SFR": "SENSOR_FAILURE",
            "SFL": "SENSOR_FLAILURE",
            "DIR": "DIRECTION_FAILURE",
            "CFG": "CONFIG_FAILURE",
            "GUD": "NOT_FAILING",
            "JRK": "JERKING"
        };
        var ftaList = Object.entries(failTypeAcronyms);
        
        var xhr = new XMLHttpRequest();
        xhr.onload = function() {
            addFHD(JSON.parse(xhr.responseText));
        }
        xhr.open("GET", "/fallible-hardware-devices");
        xhr.send();
        
        function addFHD(devices) {
            var devEntries = Object.entries(devices);
            var list = document.createElement("ul");
            list.setAttribute("style",'margin:0;padding:0');
            devEntries.forEach(x=>list.appendChild(makeFHDListItem(x)));
            parent.appendChild(list);
        }
        function makeFHDListItem(deviceEntry) {
            var li = document.createElement("li");
            li.setAttribute("style", 'display: flex;align-items: center;margin:0;padding:0;margin-bottom:1.5em;');
            var name = document.createElement("h2");
            name.textContent = deviceEntry[0];
            name.setAttribute("style",'margin:0;');
            li.appendChild(name);
            
            var buttons = document.createElement("div");
            buttons.setAttribute("style", 'display:flex;flex-grow:1;justify-content:flex-end;align-items:center;flex-gap:1em;gap:1em');
            
            for(var i = 0; i < ftaList.length; i++) {
                buttons.appendChild(makeButton(deviceEntry[0], ftaList[i]));
            }
            
            li.appendChild(buttons);
            
            return li;
        }
        function makeButton(deviceName, fta) {
            var button = document.createElement("button");
            button.setAttribute("style", 'background:var(--widget-background-color); color: var(--graph-label); font-family: monospace; border: 0; padding: 0.5em; font-size: 1.25em; letter-spacing:1px; font-weight: bold;border-radius:0.25em;');
            button.textContent = fta[0];
            button.title = fta[1];
            button.addEventListener("click", function() {
               sendCommand("sflp", configGlobals.streamID, deviceName, fta[1]);
               if(button.parentElement) {
                   Array.from(button.parentElement.children).forEach(x=>{
                       x.style.textShadow = ""
                       x.style.color = "var(--graph-label)";
                   });
                   button.style.color = "inherit";
                   button.style.textShadow = "0 0 20px var(--data-red)";
               } 
            });
            
            return button;
        }
    },
    reset: function(parent, state) {
        while(parent.children[0]) parent.removeChild(parent.children[0]);
        state.hwdevices = undefined;
    },
    ondata: function(parent, state, config) {
        
    } 
}