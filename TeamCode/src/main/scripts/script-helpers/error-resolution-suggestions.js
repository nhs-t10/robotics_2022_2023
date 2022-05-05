module.exports = function(error) {
    var sugs = getSuggestionsArray(error);
    
    if(sugs.length == 0) return "";
    else return "Suggestions: \n" + sugs.map(x=>`- ${x}`).join("\n");
}

function getSuggestionsArray(error) {
    var suggestions = [];
    
    if(error.syscall) suggestions = suggestions.concat(systemErrorSuggestions());
    
    return suggestions;
}

function systemErrorSuggestions() {
    return [
        "Delete the 'gen' directory in 'TeamCode' and allow the compiler to re-generate it",
        "Make sure that your user account has access to the filesystem",
        "Ensure that the Android Studio project is running in Trusted mode",
        "Clear up space on your hard drive"
    ];
}