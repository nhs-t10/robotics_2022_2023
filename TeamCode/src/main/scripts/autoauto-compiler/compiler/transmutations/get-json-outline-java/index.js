module.exports = function run(context) {
    context.output = getDebugJsonSettingCode(context.inputs["text-to-syntax-tree"]);
    context.status = "pass";
}

function getDebugJsonSettingCode(parsedModel) {
    var programOutline = Object.fromEntries(parsedModel.statepaths.map(x => [x.label.value, x.statepath.states.length]));
    var programOutlineJson = JSON.stringify(programOutline);

    //double-stringify it to make the JSON into valid Java
    return JSON.stringify(programOutlineJson);
}
