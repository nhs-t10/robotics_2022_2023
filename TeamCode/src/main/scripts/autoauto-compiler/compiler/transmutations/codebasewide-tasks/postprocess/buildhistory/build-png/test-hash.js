var badPercepHash = require("./bad-percep-hash");

var ins = process.argv.slice(2);

var inBuffs = ins.map(x=>Buffer.from(x, "ascii"));

var hashes = inBuffs.map(x=>badPercepHash.hash(x));

hashes.forEach(x=>console.info(x.toString("hex") + "\n"));

console.info("\n\n");

console.info(badPercepHash.combineHashes(hashes).toString("hex"));