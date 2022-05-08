const structuredSerialise = require("./index");

(function main() {
    var t = loadTests();
    testSolvedSerial(t);
    testJson(t);
})()


function testSolvedSerial(t) {
    let timeStart = Date.now();

    let solvedSerial = t.map(x => structSerialThrough(x));

    let structuredSerialiseTime = Date.now() - timeStart;


    let solvedSerialCorrectness = percentageCorrect(solvedSerial, t);

    console.warn("===");
    console.warn("Structured Serialise: ", structuredSerialiseTime);
    console.warn("Structured Serialise Correctness: ", solvedSerialCorrectness);
}

function testJson(t) {
    let timeStart = Date.now();

    let solvedJson = t.map(x => jsonThrough(x));

    let jsonTime = Date.now() - timeStart;

    console.warn("JSON: ", jsonTime);
    console.warn("JSON Correctness: ", solvedJson.map(x=>1).length / solvedJson.length);
    console.warn("===");
}

function jsonThrough(val) {
    return JSON.parse(JSON.stringify(val));
}

function structSerialThrough(val) {
    return structuredSerialise.fromBuffer(structuredSerialise.toBuffer(val));
}

function loadTests() {
    var t = [require("./test-file.json")];
    
    for(var i = 0; i < 100; i++) {
        t.push(generateRandomObject(10));
    }
    
    return t;
}

function generateRandomObject(d) {
    if (d < 0) return "";

    var o = {};
    var f = Math.random() * 10;
    for (var i = 0; i < f; i++) {
        var t = Math.random();

        if(t < 0.1) o[i] = undefined;
        if (t < 0.2) o[i] = null;
        else if (t < 0.4) o[i] = Math.random() * 3000;
        else if (t < 0.6) o[i] = (Math.random() * 3000).toString(16);
        else if (t < 0.8) o[i] = generateRandomObject(d - 1);
        else o[i] = Array.from(generateRandomObject(d - 1));

        if (t > 0.8) o.length = i + 1;
    }
    return o;
}

function percentageCorrect(solvedSerial, t) {
    return (100 * (
        solvedSerial.map((x, i) => +(JSON.stringify(x) == JSON.stringify(t[i]))).reduce((a, b) => a + b) / t.length
    )) + "%";
    
}