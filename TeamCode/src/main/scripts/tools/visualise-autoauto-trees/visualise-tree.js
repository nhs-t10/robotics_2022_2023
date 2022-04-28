var fs = require("fs");
var path = require("path");

/**
 * @typedef {object} ClassedGraphNode
 * @property {string} definition
 * @property {string} self
 * @property {string[]} depends
 * @property {number} depth
 */



 var NODE_DOT_DIAMETER = 20;
 var NODE_DOT_MARGIN = 10;
 var STROKE_WIDTH = 1;
 var VERTICAL_SPACING = 200;


//create the tree container directory if it doesn't exist.
 if(!fs.existsSync(path.join(__dirname, "trees"))) {
    fs.mkdirSync(path.join(__dirname, "trees"));
 }

/**
 * 
 * @param {ClassedGraphNode[]} nodes 
 */
var m = 0;
module.exports = function(nodes) {
    
    var svg = treeToSvg(nodes);
    var svgFile = `<svg width="${svg.width + NODE_DOT_DIAMETER/2}" height="${svg.height + NODE_DOT_DIAMETER/2}" xmlns="http://www.w3.org/2000/svg">`
    + svg.xml
    + `</svg>`;
    fs.writeFileSync(path.join(__dirname, "trees", (m++) + "tree.svg"), svgFile); //SAFE
    
}

/**
 * 
 * @param {ClassedGraphNode[]} nodes 
 */
function treeToSvg(nodes, x, y) {
    x = x||0;
    y = y||0;
    
    var xml = "", lineLayer = "";
    var totalWidth = 0;
    var totalHeight = 0;
    var nodePositions = {};
    var widths = {};
    
    totalWidth += NODE_DOT_DIAMETER;
    totalHeight += NODE_DOT_DIAMETER;
    
    nodes.forEach((node) => {
        if(!widths[node.depth]) widths[node.depth] = 0;
        widths[node.depth] += NODE_DOT_DIAMETER + NODE_DOT_MARGIN;
        
        var pos = [x + widths[node.depth] + NODE_DOT_MARGIN, y + node.depth * VERTICAL_SPACING];
        nodePositions[node.self] = pos;
        
        totalHeight = Math.max(pos[1], totalHeight);
        
        xml += `<ellipse cx="${pos[0]}" cy="${pos[1]}" rx="${NODE_DOT_DIAMETER/2}" ry="${NODE_DOT_DIAMETER/2}" style="fill:${getClassColour(node.definition)}"/>`;
    });
    
    totalWidth = Math.max(...Object.values(widths));
    
    nodes.forEach(node => {
        node.depends.forEach(dependedId => {
            var from = nodePositions[node.self];
            var to = nodePositions[dependedId];
            lineLayer += `<path d="M${from[0]} ${from[1]} L${to[0]} ${to[1]}" style="stroke:#000;stroke-width:${STROKE_WIDTH}px"/>`
        });
    });
    
    return {
        width: totalWidth,
        xml: lineLayer + xml,
        height: totalHeight
    }
}


var classColours = {};
function getClassColour(def) {
    var cl = classFromDef(def);
    if(classColours[cl]) return classColours[cl];
    else return (classColours[cl] = nextColour());
}

function classFromDef(definition) {
    return (/\w+/).exec(definition)[0];
}


var colours = (`034732-008148-c6c013-ef8a17-ef2917-4d243d-561d25-ce8147-99ddc8-f6511d-00072d-001c55-0a2472-0e6ba8-a6e1fa-b55300-ff01fb-02a9ea-b00b31-ceb992-000000-3d2645-832161-da4167-6BF178-f0f600-00e5e8-007c77-d9dbf1-d0cdd7`)
    .split("-").slice(0, 25);
function nextColour() {
    return "#" + colours.pop();    
}