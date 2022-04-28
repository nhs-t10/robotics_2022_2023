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
        var scrolledToBottom = parent.scrollHeight - Math.abs(parent.scrollTop) === parent.clientHeight;

        //override for when the scroll changes on hover
        if (state.lastScrolledToBottom && state.lastScroll == parent.scrollTop) scrolledToBottom = true;

        if (data.log) state.pre.appendChild(document.createTextNode(data.log + "\n"));

        if (scrolledToBottom) parent.scrollTop = parent.scrollHeight - parent.clientHeight;

        state.lastScroll = parent.scrollTop;
        state.lastScrolledToBottom = scrolledToBottom;
    }
};