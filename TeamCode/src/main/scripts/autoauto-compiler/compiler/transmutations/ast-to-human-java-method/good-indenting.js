var INDENTATION = "    ";

module.exports = function(src, baseLevel) {
    var indentLevel = baseLevel;

    //remove all indenting, to start
    var srcDedent = src.replace(/\r?\n[ \t]*/g, "\n");

    var inQ = false;

    var result = INDENTATION.repeat(indentLevel);
    for(var i = 0; i < srcDedent.length; i++) {
        var char = srcDedent[i];
        result += char;
        
        if(char == "\n") result += INDENTATION.repeat(indentLevel);
        else if(char == "\"") inQ = !inQ;
        else if(!inQ && char == "{") indentLevel++;
        else if(!inQ && char == "}") indentLevel--;
    }
    return result;
}