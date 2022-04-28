module.exports = {
    init: function (parent, state) {
        var pre = document.createElement("pre");
        state.pre = pre;
        parent.appendChild(pre);
    },
    reset: function (parent, state) {
        state.pre.textContent = "";
    },
    ondata: function (data, parent, state) {
        if (data.log) state.pre.appendChild(document.createTextNode(data.log + "\n"));
    }
}