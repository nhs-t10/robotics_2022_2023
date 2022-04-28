const { writeFileSync } = require("fs");
var structuredSerialise = require("./index");

// for(var i = 0; i < 100; i++) {
//     t.push(generateRandomObject(10));
// }

// function generateRandomObject(d) {
//     if(d < 0) return "";

//     var o = {};
//     var f = Math.random() * 10;
//     for(var i = 0; i < f; i++) {
//         var t = Math.random();

//         //if(t < 0.1) o[i] = o;
//         if(t < 0.2) o[i] = null;
//         else if(t < 0.4) o[i] = Math.random() * 3000;
//         else if(t < 0.6) o[i] = (Math.random() * 3000).toString(16);
//         else if(t < 0.8) o[i] = generateRandomObject(d - 1);
//         else o[i] = Array.from(generateRandomObject(d - 1));

//         if(t > 0.8) o.length = i + 1;
//     }
//     return o;
// }

var t = require("./test-file.json");

var timeStart = Date.now();

var solvedSerial = t.map(x=> {
    var p = structuredSerialise.toBuffer(x);
    return structuredSerialise.fromBuffer(p);
});

var structuredSerialiseTime = Date.now() - timeStart;


var solvedSerialCorrectness = solvedSerial.map((x,i)=>+(JSON.stringify(x)==JSON.stringify(t[i]))).reduce((a,b)=>a+b) / t.length;
solvedSerialCorrectness *= 100;
solvedSerialCorrectness += "%";

console.warn("===");
console.warn("Structured Serialise: ", structuredSerialiseTime);
console.warn("Structured Serialise Correctness: ", solvedSerialCorrectness);

timeStart = Date.now();

t.forEach(x=> {
    var p = JSON.stringify(x);
    var r = JSON.parse(p);
});

var jsonTime = Date.now() - timeStart;

console.warn("JSON: ", jsonTime);
console.warn("===");