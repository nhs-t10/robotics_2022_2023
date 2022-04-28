var characterNames = ["Dorian", "Gray", "Basil", "Hallward", "Lord", "Henry", "Harry", "Wotton", 
                "Sibyl", "Vane", "James", "Vane", 
                "Alan", "Campbell", "Lord", "Fermor", "Adrian", "Singleton"];
                
var bannedWords = ["Jew"];

var fs = require("fs");
var text = fs.readFileSync(__dirname + "/dorian.txt").toString();

var sentences = text
    .replace(/.”/g, "\”.")
    .split(".");
    
//filter the sentences to only sentences where no characters are mentioned
var nonCharacterSentences = sentences.filter(x=>!characterNames.find(y=>x.includes(y)));

//end with a period if it's not a quote; a quote and a period if it is.
var nonCharacterCorpus = nonCharacterSentences.map(x=>x.endsWith("”") ? x.substring(0, x.length - 1)+".”" : x+".").join("").split(" ");

var dissasociatedBody = {};

for(var i = 0; i < nonCharacterCorpus.length; i++) {
    console.warn(i +"/" + nonCharacterCorpus.length);
    var word = nonCharacterCorpus[i];
    
    if(bannedWords.includes(word)) continue;
    
    var indexes = findIndexesOf(word);
    var words = indexes.map(index=>nonCharacterCorpus[index + 1]).filter(x=>x!=word && !bannedWords.includes(word));
    var uniqueWords = Array.from(new Set(words));
    dissasociatedBody[word] = uniqueWords;
}

function findIndexesOf(str) {
    var idxs = [];
    for(var i = 0; i < nonCharacterCorpus.length; i++) if(nonCharacterCorpus[i] == str) idxs.push(i);
    return idxs;
}

fs.writeFileSync(__dirname + "/dorian.js", `
var dorian = ${JSON.stringify(dissasociatedBody)};
module.exports = {
    randomStart: function(num) {
        var keys = Object.keys(dorian);
        var sentenceStarts = keys.filter(x=>startsWithCapital(x));
        
        var sentence = [randomFrom(sentenceStarts)];
        
        for(var i = 0; i < num; i++) {
            var wordChances = nextWordFrom(sentence[sentence.length - 1]);
            sentence.push(randomFrom(wordChances));
        }
        
        return sentence
    },
    nextWordFrom: nextWordFrom
}

function nextWordFrom(leadin) {
    if(!dorian[leadin]) throw "no? " + leadin
    return dorian[leadin];
}

function randomFrom(arr) {
    return arr[Math.floor(Math.random() * arr.length)];
}
function startsWithCapital(str) {
    return str.charCodeAt(0) >= 65 && str.charCodeAt(0) <= 90;
}
`);