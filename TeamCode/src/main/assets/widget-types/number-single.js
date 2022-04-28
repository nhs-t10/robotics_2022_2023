var evaluateFormula = require("../helper-scripts/evaluate-formula.js");

module.exports = {
    init: function (parent, state) {
        state.num = document.createElement("h3");
        state.caption = document.createElement("span");

        parent.appendChild(state.caption);
        parent.appendChild(state.num);

        parent.classList.add("nopadding");
        parent.classList.add("nooverflow");
        parent.classList.add("center");
    },
    reset: function (parent, state) {
        state.num.textContent = "";
        state.caption.textContent = "";
    },
    ondata: function (data, parent, state, config) {
        if (data.fields === undefined) return false;

        var num = data.fields[config.field];

        try {
            num = evaluateFormula(config.formula, num);
        } catch(e) {}

        var numRounded = config.base10Precision ? (Math.round(num / config.base10Precision) * config.base10Precision).toString() : num.toString();

        var greaterPart = numRounded.split(".")[0];
        var lesserPart = numRounded.split(".")[1];
        //if there's a decimal part...
        if (greaterPart != numRounded) {
            var decimalPartRoundingLength = config.base10Precision ? (config.base10Precision.toString().split(".")[1] || "").length : lesserPart.length;
            numRounded = greaterPart + "." + lesserPart.substring(0, decimalPartRoundingLength);
        }

        state.num.textContent = numRounded;
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
        },
        {
            name: "formula",
            type: "text",
            default: "x"
        },
        {
            name: "base10Precision",
            type: "number",
            default: 0.001
        }
    ]
}