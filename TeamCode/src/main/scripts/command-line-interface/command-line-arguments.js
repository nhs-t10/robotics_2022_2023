module.exports = function(schema) {
    const out = {};
    const aliasMap = mapShortToLong(schema);
    processAllArgv(schema, process.argv, aliasMap, out);
    cascadeDefaults(schema, out);
    return out;
}

function cascadeDefaults(schema, out) {
    for(const x in schema) {
        if(!(x in out)) out[x] = schema[x].value;
    }
}

function mapShortToLong(schema) {
    var m = {};
    Object.entries(schema).forEach(x=>{
        x[1].short.forEach(y=>m[y] = x[0]);
    });
    return m;
}

function processAllArgv(schema, argv, aliasMap, out) {
    //skip argv[0], since that's the name of the script
    for(var i = 1; i < argv.length; i++) {
        processArg(schema, argv[i], aliasMap, out);
    }
}

function processArg(schema, arg, aliasMap, out) {
    if(arg.startsWith("--")) processLongFlag(schema, arg, out);
    else if(arg.startsWith("-")) processAliases(schema, arg, aliasMap, out);
}

function processLongFlag(schema, arg, out) {
    //remove the `--`
    var a = arg.substring(2);

    var keyVal = a.split("=");
    var key = keyVal[0];
    var val = keyVal[1];

    if(keyVal.length == 1) val = "true";

    if (!schema[key]) errorNoFlag(key);
    else out[key] = castToSchema(schema[key].value, val);
}

function castToSchema(typeDesired, arg) {
    switch(typeof typeDesired) {
        case "number": return (+arg) || 0;
        case "boolean": return arg == "true";
        default: return arg;
    }
}

function processAliases(schema, arg, aliasMap, out) {
    var lastKey = "";
    //start at 1 to cut off the `-`
    for(var i = 1; i < arg.length; i++) {
        var alias = arg[i];

        if(alias == "=") {
            if(lastKey) {
                out[lastKey] = castToSchema(schema[lastKey].value, arg.substring(i + 1));
            }
            break;
        }


        var k = aliasMap[alias];
        if (!k) errorNoFlag(alias);

        lastKey = k;
        out[k] = true;
    }
}

function errorNoFlag(flag) {
    console.error(`No command-line flag '${flag}'`);
    process.exit(1);
}