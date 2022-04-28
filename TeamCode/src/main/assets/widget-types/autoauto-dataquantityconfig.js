module.exports =  {
    init: function () {

    },
    ondata: function (data, parent, state, config) {
        //only send a request when it changes
        if (config.dataTransferAmount != configGlobals.autoautoDataTransferAmount) {
            configGlobals.autoautoDataTransferAmount = config.dataTransferAmount;
            var commands = {
                "Don't send data": "auauds",
                "Send all data": "auaus",
                "Send simplified program outline": "auaubs"
            };
            if (commands[config.dataTransferAmount]) sendCommand(commands[config.dataTransferAmount], configGlobals.streamID);
        }
    },
    reset: function () { },
    config: [
        {
            name: "dataTransferAmount",
            type: "select",
            value: ["Don't send data", "Send all data", "Send simplified program outline"]
        }
    ]
}