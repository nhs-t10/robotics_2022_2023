module.exports = function(context) {
    context.writtenFiles[context.resultFullFileName] = context.lastInput;
    context.status = "pass";
}