module.exports = {
    init: function (parent, state) {
        state.caption = document.createElement("span");
        state.field = document.createElement("p");

        parent.appendChild(state.caption);
        parent.appendChild(state.field);

        parent.classList.add("nopadding");
        parent.classList.add("center");
    },
    reset: function (parent, state) {
        state.field.textContent = "";
        state.caption.textContent = "";
    },
    ondata: function (data, parent, state, config) {
        if (data.fields === undefined) return false;

        state.field.textContent = data.fields[config.field];
        state.caption.textContent = config.label;
    },
    config: [
        {
            name: "field",
            type: "select",
            value: "field"
        },
        {
            name: "label",
            type: "text",
            default: ""
        }
    ]
}