"use strict";

module.exports = function run(context) {
    context.output = invertControlGraph(context.inputs["build-cgraph"]);
    context.status = "pass";
}

function invertControlGraph(cgraph) {
    var igc = {};

    Object.entries(cgraph).forEach(x => {
        const fromId = x[0];
        const destinations = x[1];
        
        //required, since that way every block will have an entry.
        if(!igc[fromId]) igc[fromId] = [];

        destinations.forEach(to => {
            const toId = to.label;
            if (!igc[toId]) igc[toId] = {};

            igc[toId][fromId] = Object.assign({}, to, {label: fromId});
        });
    });
    
    for(const key in igc) {
        igc[key] = Object.values(igc[key]);
    }
    
    return igc;
}