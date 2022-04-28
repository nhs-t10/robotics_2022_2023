module.exports = {
    init: function (parent, state) {
        var pre = document.createElement("pre");
        state.pre = pre;
        parent.appendChild(pre);
    },
    reset: function (parent, state) {
        if (state.pre) state.pre.textContent = "";
        state.lastScroll = 0;
        parent.scrollTop = 0;
    },
    ondata: function (data, parent, state) {
        var scrolledToBottom = parent.scrollHeight - Math.abs(parent.scrollTop) === parent.clientHeight;

        //override for when the scroll changes on hover
        if (state.lastScrolledToBottom && state.lastScroll == parent.scrollTop) scrolledToBottom = true;

        state.pre.appendChild(document.createTextNode(JSON.stringify(data) + "\n"));

        if (scrolledToBottom) parent.scrollTop = parent.scrollHeight - parent.clientHeight;

        state.lastScroll = parent.scrollTop;
        state.lastScrolledToBottom = scrolledToBottom;
    }
}