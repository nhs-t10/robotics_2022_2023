var sentenceGenerator = require("./sentence-generator");

var simpleNouns = require("./words/simple-nouns.js");

var SIMPLE_NOUNS_LIMIT = 3;

module.exports = {
    complexPhrase: complexPhraseHash,
    simpleNouns: simpleNounHash
}

function simpleNounHash(hash) {
    var hashCharPerWord = Math.ceil(Math.floor(Math.log2(simpleNouns.length)) / 4);
    
    var words = [];
    for(var i = 0; i < hash.length; i+= hashCharPerWord) {
        var code = hash.substring(i, i + hashCharPerWord);
        var numerical = +('0x' + code);
        
        words.push(getSimpleNoun(numerical));
        
        if(words.length >= SIMPLE_NOUNS_LIMIT) break;
    }
    return words.join(" ");
}

function complexPhraseHash(hash) {
    hash = hash + "";
    return sentenceGenerator(hash);
}

function getSimpleNoun(id) {
    return simpleNouns[id % simpleNouns.length];
}