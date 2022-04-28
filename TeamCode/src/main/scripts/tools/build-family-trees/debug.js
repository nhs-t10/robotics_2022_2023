var fs = require("fs");

var structuredSerialize = require("../../script-helpers/structured-serialise");


var gp = structuredSerialize.fromBuffer(fs.readFileSync(__dirname + "/vis.sserial"));


var traversed = new Map();

var b = basicTraverse(gp);


function basicTraverse(x, path) {
    path = path || "";

    if(traversed.has(x)) throw "recursed! " + path + " == " + traversed.get(x);
    traversed.set(x, path);

    var s = "";
    s += x.name + "\n";
    if(x.relations.partner) s += x.relations.partner.name + "\n";

    x.relations.children.forEach((y,i)=>s+=basicTraverse(y, path + "/children/" + i));

    return s;
}