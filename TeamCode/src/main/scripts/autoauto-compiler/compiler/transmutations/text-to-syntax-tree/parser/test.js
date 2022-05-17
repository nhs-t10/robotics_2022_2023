const path = require("path");
const fs = require("fs");


const folderScanner = require("../../../folder-scanner");

const pegjsParse = require("../aa-parser").parse;

const prattParse = require("./index");



const fscan = folderScanner(path.join(__dirname, "../../../../../.."), ".autoauto");

//console.log(Array.from(lexer(`1*f`)))

tryPratt(`delegate("fif", 3)`);


(async function() {
    for await(const file of fscan) {
        console.log(file);
        const content = fs.readFileSync(file).toString();
        fs.writeFileSync("./a.json", tryPratt(content));
        fs.writeFileSync("./b.json", tryPegjs(content));
        break;
    }
})();

function diff(a, b) {
    const la = a.split("\n");
    const lb = b.split("\n");
    
    for(var i = 0; i < Math.max(la.length, lb.length); i++) {
        if(la[i] != lb[i]) {
            console.log(la[i]);
        }
    }
}

function tryPegjs(content) {
    try {
        return JSON.stringify(pegjsParse(content), null, 1);
    } catch(e) {
        console.log(e);
    }
}

function tryPratt(content) {
    try {
        return JSON.stringify(prattParse(content), null, 1);
    } catch(e) {
        console.log(e);
    }
}