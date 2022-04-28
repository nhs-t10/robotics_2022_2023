module.exports = function(schema) {
    var aliasMap = mapShortToLong(schema);
    processAllArgv(schema, process.argv, aliasMap);
    cascadeDefaults(schema);
}

function cascadeDefaults(schema) {
    Object.keys(schema).forEach(x=> {
        if(typeof schema[x] === "object") schema[x] = schema[x].value;
    });
}

function mapShortToLong(schema) {
    var m = {};
    Object.entries(schema).forEach(x=>{
        x[1].short.forEach(y=>m[y] = x[0]);
    });
    return m;
}

function processAllArgv(schema, argv, aliasMap) {
    //skip argv[0], since that's the name of the script
    for(var i = 1; i < argv.length; i++) {
        processArg(schema, argv[i], aliasMap);
    }
}

function processArg(schema, arg, aliasMap) {
    if(arg.startsWith("--")) processLongFlag(schema, arg, aliasMap);
    else if(arg.startsWith("-")) processAliases(schema, arg, aliasMap);
}

function processLongFlag(schema, arg, aliasMap) {
    //remove the `--`
    var a = arg.substring(2);

    var keyVal = a.split("=");
    var key = keyVal[0];
    var val = keyVal[1];

    if(keyVal.length == 1) val = "true";

    if(!schema[key]) throw `No command-line flag '${key}'.`;
    else schema[key] = castToSchema(schema[key].value, val);
}

function castToSchema(typeDesired, arg) {
    switch(typeof typeDesired) {
        case "number": return (+arg) || 0;
        case "boolean": return arg == "true";
        default: return arg;
    }
}

function processAliases(schema, arg, aliasMap) {
    var lastKey = "";
    //start at 1 to cut off the `-`
    for(var i = 1; i < arg.length; i++) {
        var alias = arg[i];

        if(alias == "=") {
            if(lastKey) {
                schema[lastKey] = arg.substring(i + 1);
            }
            break;
        }


        var k = aliasMap[alias];
        if(!k) throw `No command-line flag '${alias}'`;

        lastKey = k;
        schema[k] = true;
    }
}