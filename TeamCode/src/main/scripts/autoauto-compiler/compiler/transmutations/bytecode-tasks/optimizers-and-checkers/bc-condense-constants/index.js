module.exports = function run(context) {
    var bcBlocks = context.inputs["syntax-tree-to-bytecode"].blocks;

    Object.values(bcBlocks).forEach(x => {
        if (!x.jumps.forEach) {
            console.error(x);
            throw "bad block!"
        }
        modifyBcCondenseConstants(x.jumps);
        modifyBcCondenseConstants(x.code);
    });

    context.output = bcBlocks;
    context.status = "pass";
}


function modifyBcCondenseConstants(code) {
    code.forEach(x => {
        if (!x.args) x.args.map(x => x);
        modifyBcCondenseConstants(x.args);
        condenseConstantOps(x);
    });
}

function condenseConstantOps(codeObject) {
    if (isDecidableOpWithTwoArgs(codeObject.code)) {
        if (codeObject.args.length != 2) {
            throw {
                text: "Malformed bytecode: improper amount of arguments for an operation",
                location: codeObject.location
            }
        }

        var left = codeObject.args[0];
        var right = codeObject.args[1];

        if (left.hasOwnProperty("__value") && right.hasOwnProperty("__value")) {
            var r = doOp(codeObject.code, left.__value, right.__value);
            if (r != r) r = undefined;

            codeObject.code = left.code;
            codeObject.__value = r;
            codeObject.args = [];
        }
    }
}

function isDecidableOpWithTwoArgs(code) {
    return code >= 0x200 && code <= 0x20B;
}

function doOp(code, left, right) {
    switch (code) {

        case 0x200: //add
            return left + right;
        case 0x201: //subtr
            return left - right;
        case 0x202: //mul
            return left * right;
        case 0x203: //div
            return left / right;
        case 0x204: //mod
            return left % right;
        case 0x205: //exp
            return Math.pow(left, right);
        case 0x206: //cmp_lt
            return left < right;
        case 0x207: //cmp_lte
            return left <= right;
        case 0x208: //cmp_eq
            return left == right;
        case 0x209: //cmp_neq
            return left != right;
        case 0x20A: //cmp_gte
            return left >= right;
        case 0x20B: //cmp_gt
            return left > right;
        default:
            throw "unknown math op"
    }
}