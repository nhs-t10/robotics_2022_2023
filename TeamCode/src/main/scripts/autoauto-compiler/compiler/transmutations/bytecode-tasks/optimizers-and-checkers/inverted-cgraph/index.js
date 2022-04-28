module.exports = function run(context) {
    context.output = invertControlGraph(context.inputs["build-cgraph"]);
    context.status = "pass";
}

function invertControlGraph(cgraph) {
    var igc = {};

    Object.entries(cgraph).forEach(x => {
        var from = x[0];
        var tos = x[1];

        if (!igc[from]) igc[from] = [];

        tos.forEach(to => {
            if (!igc[to]) igc[to] = [];

            if (!igc[to].includes(from)) igc[to].push(from);
        });
    });
    return igc;
}