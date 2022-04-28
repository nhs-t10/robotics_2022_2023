const bytecodeSpec = require("../../bytecode-spec");

module.exports = function run(context) {
    var heirarchalBytecode = context.inputs["bc-condense-constants"];

    context.output = buildGraphFrom(heirarchalBytecode);
    context.status = "pass";
}

function buildGraphFrom(bcode) {
    var keys = Object.keys(bcode);

    var stateKeys = keys.filter(x => /^s\/[^/]+\/0$/.test(x));

    var r = {};
    for (var i = 0; i < keys.length; i++) {
        r[keys[i]] = findBlockTargets(bcode[keys[i]], stateKeys, keys);
    }

    return r;
}

function findBlockTargets(block, allStateBlockLabels, allBlockLabels) {

    var jumpLabelCodes = [bytecodeSpec.jmp_l.code, bytecodeSpec.jmp_l_cond.code, bytecodeSpec.yieldto_l.code];


    var res = [];

    for (var i = 0; i < block.jumps.length; i++) {
        if (!jumpLabelCodes.includes(block.jumps[i].code)) {
            throw {
                text: `Malformed bytecode: non-jump root (0x${block.jumps[i].code.toString(16)}) in jumps section.`,
                location: block.jumps[i].location
            }
        }

        var jumpArgs = block.jumps[i].args;

        var staticJmpTrgt = jumpArgs[jumpArgs.length - 1].__value;

        //use slice to make a clone. That way, `allStateBlockLabels` won't be shared across different jump targets.
        var jumpTargets = staticJmpTrgt ? [staticJmpTrgt] : allStateBlockLabels.slice();

        checkJumpTargetsExist(jumpTargets, allBlockLabels, block.jumps[i].location);

        res = res.concat(jumpTargets);
    }

    var uniqdTargets = Array.from(new Set(res));
    return uniqdTargets;
}

function checkJumpTargetsExist(targets, validTargets, reportingLocation) {
    var isValid = true, invalidCause;
    for (var i = 0; i < targets.length; i++) {
        if (validTargets.indexOf(targets[i]) == -1) {
            isValid = false;
            invalidCause = targets[i];
            break;
        }
    }

    if (!isValid) {
        throw {
            kind: "ERROR",
            text: "Unable to find `" + invalidCause + "` as a jump-location. Please make sure that you didn't make a typo in your 'goto' statement!",
            location: reportingLocation
        }
    }
}