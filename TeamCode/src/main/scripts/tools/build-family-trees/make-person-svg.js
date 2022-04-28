var crypto = require("crypto");
var familyColors = require("./family-colors");

module.exports = function(build, x, y, size) {
    
    var heraldry = familyColors.fullColorScheme(build);
    
    var rand = seededRandom(build.name);
    
    var personOutline = `<path
    d="M -1.0667615e-5,0.10792 C -0.32522511,0.10792 -0.58703604,0.36975 -0.58703604,0.69496 V 0.97671 A 1,1.2000001 0 0 0 -0.0049165,1.20227 1,1.2000001 0 0 0 0.58703629,0.96769 V 0.69496 c 0,-0.32521 -0.26183239,-0.58704 -0.587046957615,-0.58704 z"/>
 <path
    d="m -0.52801507,0.39293 c -0.0571078,0 -0.10308767,0.046 -0.10308767,0.10309 v 0.4315 a 1,1.2000001 0 0 0 0.24157075,0.17545 V 0.49602 c 0,-0.0571 -0.0459583,-0.10309 -0.10306618,-0.10309 z"/>
 <path
    d="m 0.49259846,0.39293 c -0.0571079,0 -0.1030877,0.046 -0.1030877,0.10309 V 1.10161 A 1,1.2000001 0 0 0 0.63110299,0.92773 V 0.49602 c 0,-0.0571 -0.0459797,-0.10309 -0.10308767,-0.10309 z"/>
 <circle
    cx="0"
    cy="-0.1"
    r="0.6125" />`;
    
    var hair = randomHair(rand, heraldry);
    
    return `<g transform="translate(${x} ${y}) scale(${size / 2})" fill="${heraldry.primaryDark}">${hair}${personOutline}</g>`;
}

function randomHair(rand, heraldry) {
    var hairPieces = generateHair(rand);
    
    return `<path d="${hairPieces}" fill="${heraldry.primary}" stroke-width="1" stroke="${heraldry.primaryPastel}"/>`
}
function generateHair(rand) {
    var p = "M 0.5 0";
    
    var N_POTS = 5;
    
    var MAXS = rand() > 0.5 ? Math.PI : Math.PI * 2;
    
    var hS = Math.PI / N_POTS;
    
    for(var i = 0; i <= MAXS; i += Math.PI * 2 / N_POTS) {
        var rX = Math.cos(i + hS * rand()) * (1 + rand()),
            rY = -Math.sin(i + hS * rand()) * (1 + rand());
            
        var nextX = Math.cos(i),
            nextY  = -Math.sin(i);
        p += `Q ${r5(rX/2)} ${r5(rY/2)} ${r5(nextX/2)} ${r5(nextY/2)}`;
    }
    return p;
}

function r5(n) {
    return Math.round(n * 10000) / 10000;
}


function seededRandom(seed) {
    seed = parseInt(sha(seed).substring(0,8), 16);
    
    return function mulberry32random() {
        var t = seed += 0x6D2B79F5;
        t = Math.imul(t ^ t >>> 15, t | 1);
        t ^= t + Math.imul(t ^ t >>> 7, t | 61);
        return ((t ^ t >>> 14) >>> 0) / 4294967296;
    }
}

function sha(t) {
    return crypto.createHash("sha256").update(t + "").digest("hex");
}