module.exports = function(block) {
    return [].concat(
        flattenArrayOfBc(block.code),
        flattenArrayOfBc(block.jumps)
    );
}

function flattenArrayOfBc(bcs) {
    return bcs.map(x=>flattenBc(x)).flat(1);    
}

function flattenBc(bc) {
    var a = flattenArrayOfBc(bc.args);
    bc.args = [];
    return a.concat([bc]);
}