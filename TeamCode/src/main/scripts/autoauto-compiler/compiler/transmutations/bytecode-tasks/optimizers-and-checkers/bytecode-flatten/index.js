var calculateBlockLength = require("./block-length");
var replaceLabelWithIndex = require("./label-to-index");
var flattenBlock = require("./block-flatten");
var makeOrderedBlocks = require("./ordered-block-array");

module.exports = function run(context) {
    var bytecode = context.lastInput;
    var blocks = makeOrderedBlocks(bytecode);

    //calculate how long each block will be, when it's flattened
    blocks.forEach(x => x.length = calculateBlockLength(x));

    //use that to calculate each block's offset
    var o = 0;
    blocks.forEach(x => {
        x.offset = o;
        o += x.length;
    });

    //replace labeled jumps with bytecode-number jumps
    blocks.forEach(x => replaceLabelWithIndex(x, bytecode));

    //and finally, flatten! this WILL remove blocks.
    var flatBc = blocks.map(x => flattenBlock(x)).flat(1);

    context.output = flatBc;
    context.status = "pass";
}