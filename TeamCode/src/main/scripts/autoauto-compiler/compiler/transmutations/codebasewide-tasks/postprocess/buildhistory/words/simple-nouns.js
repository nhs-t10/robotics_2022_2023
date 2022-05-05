var fullNouns = require("./nouns.json");

//simple nouns are nouns with less than 3 vowel parts, not counting silent "e" at the end
module.exports = fullNouns.filter(x=> {
    return vowelCount(x.toLowerCase().replace(/e$/, "")) < 3
});

function vowelCount(word) {
    return word.match(/[aeiou]+/g).length
}