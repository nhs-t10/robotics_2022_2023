module.exports = function(context) {
    context.output = getCompatModeSetter(context.fileFrontmatter);
    context.status = "pass";
}

function getCompatModeSetter(frontMatter) {
    var keys = Object.keys(frontMatter);

    var flagRegex = /^[a-z]*flag_/;
    var flagPrefix = "\t@";

    var flagKeys = keys.filter(x=>flagRegex.test(x));

    var setters = flagKeys.map(x=>`runtime.rootModule.globalScope.systemSet(${jStringEnc(flagPrefix + x)}, new AutoautoBooleanValue(true));`);

    return setters.join("\n");
}

function jStringEnc(str) {
    return JSON.stringify("" + str);
}