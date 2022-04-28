module.exports = {
    init: function (parent, state) {
        state.num = document.createElement("h3");
        var deltaParent = document.createElement("span");

        state.deltaIcon = document.createElement("span");
        state.delta = document.createElement("span");

        deltaParent.appendChild(state.deltaIcon);
        deltaParent.appendChild(state.delta);

        parent.appendChild(state.num);
        parent.appendChild(deltaParent);

        parent.classList.add("nopadding");
        parent.classList.add("nooverflow");
        parent.classList.add("center");
    },
    reset: function (parent, state) {
        state.lastConnectionLag = 0;
        state.delta.textContent = "";
        state.deltaIcon.textContent = "";
    },
    ondata: function (data, parent, state) {
        var connectionLag = Date.now() - data.time;

        if (state.lastConnectionLag === undefined) state.lastConnectionLag = connectionLag;

        state.num.textContent = connectionLag + "ms";

        var deltaLag = ((connectionLag - state.lastConnectionLag) / state.lastConnectionLag) * 100;
        if (deltaLag > 0) {
            state.deltaIcon.textContent = "+";
            state.delta.style.color = "var(--data-green)";
        } else {
            state.deltaIcon.textContent = "";
            state.delta.style.color = "var(--data-red)";
        }
        state.delta.textContent = Math.round(deltaLag) + "%";

        state.lastConnectionLag = connectionLag;
    }
}