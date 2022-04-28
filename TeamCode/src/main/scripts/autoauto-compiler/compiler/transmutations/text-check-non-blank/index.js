module.exports = function(context) {
    var fileContent = context.lastInput;
            
    if(fileContent.trim() == "") throw "Autoauto files shouldn't be empty";
    else context.status = "pass";
}