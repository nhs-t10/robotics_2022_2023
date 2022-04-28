var evaluateFormula = require("../helper-scripts/evaluate-formula.js");

module.exports = {
    init: function (parent, state) {
        state.num = document.createElement("h3");
        var deltaParent = document.createElement("span");

        state.deltaIcon = document.createElement("span");
        state.delta = document.createElement("span");
        state.caption = document.createElement("span");

        deltaParent.appendChild(state.deltaIcon);
        deltaParent.appendChild(state.delta);

        parent.appendChild(state.caption);
        parent.appendChild(state.num);
        parent.appendChild(deltaParent);

        parent.classList.add("nopadding");
        parent.classList.add("nooverflow");
        parent.classList.add("center");
    },
    reset: function (parent, state) {
        state.lastNum = 0;

        state.delta.textContent = "";
        state.caption.textContent = "";
        state.deltaIcon.textContent = "";
    },
    ondata: function (data, parent, state, config) {
        var num = data.fields[config.field] || 0;

        try {
            num = evaluateFormula(config.formula, num, +state.lastNum);
        } catch(e) {}

        if (state.lastNum === undefined) state.lastNum = num;

        var numRounded =config.base10Precision ? (Math.round(num / config.base10Precision) * config.base10Precision).toString() : num.toString();

        var greaterPart = numRounded.split(".")[0];
        var lesserPart = numRounded.split(".")[1];
        //if there's a decimal part...
        if (greaterPart != numRounded) {
            var decimalPartRoundingLength = config.base10Precision ? (config.base10Precision.toString().split(".")[1] || "").length : lesserPart.length;
            numRounded = greaterPart + "." + lesserPart.substring(0, decimalPartRoundingLength);
        }

        state.num.textContent = numRounded;

        var deltaPercent = ((num - state.lastNum) / state.lastNum) * 100;
        if (deltaPercent > 0) {
            state.deltaIcon.textContent = "+";
            state.delta.style.color = "var(--data-green)";
        } else {
            state.deltaIcon.textContent = "";
            state.delta.style.color = "var(--data-red)";
        }
        state.delta.textContent = Math.round(deltaPercent) + "%";

        state.caption.textContent = config.label;

        state.lastNum = num;
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