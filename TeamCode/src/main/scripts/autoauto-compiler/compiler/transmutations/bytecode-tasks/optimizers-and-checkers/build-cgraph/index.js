"use strict";

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

    var functionJumps = scanForFunctionDefs(block.code);

    //the `functionJumps` array isn't used anywhere else, so it's ok to reuse it as `res`.
    var res = functionJumps;

    for (var i = 0; i < block.jumps.length; i++) {
        const bc = block.jumps[i];
        if (!jumpLabelCodes.includes(bc.code)) {
            throw {
                text: `Malformed bytecode: non-jump root (0x${bc.code.toString(16)}) in jumps section.`,
                location: block.jumps[i].location
            }
        }

        var jumpArgs = bc.args;

        var staticJmpTrgt = jumpArgs[jumpArgs.length - 1].__value;

        var jumpTargets = staticJmpTrgt ? [{label: staticJmpTrgt, type: bc.code}] : allStateBlockLabels.map(x=>({ label: x, type: bc.code, dynamic: true }));

        checkJumpTargetsExist(jumpTargets, allBlockLabels, bc.location);

        res = res.concat(jumpTargets);
        
        //if this was an unconditional jump, there can't be any jumps after it.
        if (bc.code == bytecodeSpec.jmp_l.code) break;
    }

    var uniqdTargets = Array.from(new Set(res));
    return uniqdTargets;
}

function scanForFunctionDefs(codes) {
    const defs = [];
    
    for (const bc of codes) {
        if(bc.code == bytecodeSpec.makefunction_l.code) {
            const jumpTo = bc.args[0].__value;
            
            
            if(!jumpTo) throw {
                text: "Malformed bytecode: bad function definition",
                location: bc.location
            };
            
            defs.push({
                label: jumpTo, 
                type: bc.code
            });
        }
        defs.push(...scanForFunctionDefs(bc.args));
    }
    
    return defs;
}

function checkJumpTargetsExist(targets, validTargets, reportingLocation) {
    var isValid = true, invalidCause;
    for (var i = 0; i < targets.length; i++) {
        if (validTargets.indexOf(targets[i].label) == -1) {
            isValid = false;
            invalidCause = targets[i].label;
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