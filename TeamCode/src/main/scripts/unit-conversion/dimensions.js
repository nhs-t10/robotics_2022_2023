module.exports = {
    getHumanDescriptionOfDimension: getHumanDescriptionOfDimension
}

function getHumanDescriptionOfDimension(dim) {
    var parsed = parseDimension(dim);
    var sorted = parsed.sort((a,b)=>b[1] - a[1]);
    
    var ans = "", ansHasPer = false;
    
    for(var i = 0; i < sorted.length; i++) {
        
        var q = sorted[i][1];
        var d = sorted[i][0];
        if (q < 0 && !ansHasPer) {
            ans += " per";
            ansHasPer = true;
        }
        if(q != 0) {
            ans += " " + describeDimensionLetter(d) + describeExp(q);
        }
    }
    
    if(ans == "") return "a unitless quantity";
    return ans.substring(1);
}

function describeExp(exp) {
    exp = Math.abs(exp);
    
    if(exp == 1) return "";
    else if(exp == 2) return " squared";
    else if(exp == 3) " cubed";
    else return " to the " + ordinal(exp) + " power";
}

function ordinal(num) {
    var ld = num % 10;
    
    //11th, 12th, and 13th-- not 11st, 12nd, and 13rd.
    if(num > 10 && num < 14) return num + "th"; 
    
    if(ld == 1) return num + "st";
    else if(ld == 2) return num + "nd";
    else if(ld == 3) return num + "rd";
    else return num + "th"; 
}

function describeDimensionLetter(ltr) {
    return ({
        T: "time",
        I: "electric current",
        M: "mass",
        C: "temperature",
        J: "luminous intensity",
        A: "planar angle",
        N: "amount of substance",
        L: "length"
    })[ltr];
}

/**
 * 
 * @param {*} dimSpec 
 * @returns {Dimension}
 */
function parseDimension(dimSpec) {
    var resultPowers = {
        L: 0,
        T: 0,
        M: 0,
        I: 0,
        C: 0,
        J: 0,
        A: 0,
        N: 0
    };
    
    var ds = (dimSpec + "").split(" ");
    
    for(var i = 0; i < ds.length; i++) {
        var dsp = ds[i].split("^");
        if(resultPowers.hasOwnProperty(dsp[0])) {
            resultPowers[dsp[0]] = (+dsp[1]) || 1;
        }
    }
    return Object.entries(resultPowers);
}

/**
 * @typedef {(string|number)[][]} Dimension
 * 
 */
