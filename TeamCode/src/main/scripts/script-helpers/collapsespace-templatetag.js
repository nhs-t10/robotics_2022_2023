module.exports = function(strs) {
    var assembled = "";
    for(var i = 0; i < strs.length - 1; i++) {
        assembled += strs[i].replace(/\r?\n\s+/g, " ") + arguments[i + 1];
    }
    assembled += strs[strs.length - 1].replace(/\r?\n\s+/g, " ");
    
    return assembled;
}