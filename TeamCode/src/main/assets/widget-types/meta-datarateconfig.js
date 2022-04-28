module.exports = {
    init: function () {

    },
    ondata: function (data, parent, state, config) {
        //only send a request when it changes
        if (config.sendsPerSecond != configGlobals.sendsPerSecond) {
            configGlobals.sendsPerSecond = config.sendsPerSecond;
            sendCommand("sps", configGlobals.streamID, config.sendsPerSecond);
        }
    },
    reset: function () { },
    config: [
        {
            name: "sendsPerSecond",
            type: "number",
            default: 30
        }
    ]
}