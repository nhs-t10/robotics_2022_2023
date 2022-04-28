module.exports = function(bytecode) {
    var entry = bytecode.ENTRY;
    var others = Object.values(bytecode).filter(x=>x!=entry);
    
    return [entry].concat(others);
}