var corpus = require("./text/dorian.js");

/**
 * 
 * @param {Buffer} seeder 
 */
module.exports = function(seeder) {
    var sentence = corpus.randomStart(2);
    var lastWord = sentence[sentence.length - 1];
    var seedReadIndex = 0;
    while(true) {
        var nextWordPossibilities = corpus.nextWordFrom(lastWord);
        
        var seedNumber = seeder.readUInt8(seedReadIndex);
        seedReadIndex++;
        var nextWord = nextWordPossibilities[seedNumber % nextWordPossibilities.length];
        
        lastWord = nextWord;
        sentence.push(nextWord);
        
        if(seedReadIndex == seeder.byteLength) break;
        
    }
    var sentenceStr = sentence.join(" ");
    var singleLine = sentenceStr.replace(/[\r\n ]+/g, " ");
    var lastSentenceEndIndex = Math.max(singleLine.lastIndexOf("."), singleLine.lastIndexOf("!"), singleLine.lastIndexOf("”"), singleLine.lastIndexOf("?"))
    
    if(lastSentenceEndIndex > -1) return singleLine.substring(0, lastSentenceEndIndex + 1);
    else return singleLine;
}