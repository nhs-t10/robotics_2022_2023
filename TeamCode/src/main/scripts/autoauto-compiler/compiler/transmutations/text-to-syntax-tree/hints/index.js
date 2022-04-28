var hintFunctions = [gitMerge];

module.exports = function(fileContent, syntaxError) {
    var hints = [];
    for(var i = 0; i < hintFunctions.length; i++) {
        var h = hintFunctions[i](fileContent, syntaxError);
        if(h) hints.push("hint: " + h);
    }
    return hints;
}

function gitMerge(fileContent, syntaxError) {
    var m = "this looks like an unfinished Git merge. Make sure you correct all merge errors!";
    
    if(syntaxError.found == ">") {
        if(fileContent.startsWith(">>>>", syntaxError.location.start.offset)) {
            return m;
        }
    } else if(syntaxError.found == "<") {
        if (fileContent.startsWith("<<<<", syntaxError.location.start.offset)) {
            return m;
        }
    } else if(syntaxError.found == "=") {
        if (fileContent.startsWith("====", syntaxError.location.start.offset)) {
            return m;
        }
    }
}