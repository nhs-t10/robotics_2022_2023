

/**
 * 
 * @param {string} formula 
 * @param {formulaArgs|number} args 
 * @param {number[]|number} [oldData]
 * @returns {number}
 */
module.exports = function(formula, args, oldData) {
    if(typeof args === "number") args = {
        values: [args],
        data: []
    };

    if(oldData !== undefined) {
        args.data = (typeof oldData === "number") ? [oldData] : oldData;
    }

    var functionSrc = `return ((${args.values.map((x,i)=>varLetterIndex(i)).join(",")},datapoints, time)=>${formula});`;

    return (new Function(functionSrc))().call(null, ...args.values.concat([args.data, Date.now() / 1000]));
}

var varLetters = ["x", "y", "z", "a", "b", "c", "d", "e", "f", "g", "h"];

function varLetterIndex(i) {
    return varLetters[i] || "arg" + i;
}

/**
 * @typedef {object} formulaArgs
 * @property {number[]} values
 * @property {number[]} data
 */