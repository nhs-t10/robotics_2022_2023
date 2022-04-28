var https = require("https");
var fs = require("fs");
var androidStudioLogging = require("../script-helpers/android-studio-logging");

var query = `SELECT DISTINCT ?itemLabel ?conv ?convUnitLabel ?convAppLabel ?sym WHERE {
    ?item wdt:P31/wdt:P279* wd:Q47574.
    ?item p:P2370 ?qq.
    ?qq psv:P2370 [ wikibase:quantityAmount     ?conv ;
                   wikibase:quantityUnit       ?convUnit ].
    OPTIONAL {
         ?qq pq:P518 ?convApp.
    }
    MINUS {
      ?qq pq:P2241 ?deprecation
    }
    SERVICE wikibase:label { bd:serviceParam wikibase:language "en". }
    ?item wdt:P5061 ?sym.
}`

module.exports = function (v) {
    downloadData(v);
}


function downloadData(version, cb) {
    https.get({
        headers: {
            "User-Agent": "Autoauto Unit Conversion Updater"
        },
        hostname: "query.wikidata.org",
        path: "/bigdata/namespace/wdq/sparql?format=json&query=" + encodeURIComponent(query)
    }, function(res) {
        if(res.statusCode == 200) {
            var d = "";
            res.on("data", c => d += c);
            res.on("end", function() {
                parseJsonSaveCache(d, version, cb || (x=>x));
            });
        } else {
            androidStudioLogging.warning("Couldn't update unit-conversion tables from Wikidata");
        }
    });
}

function parseJsonSaveCache(d, v, cb) {
    try {
        var p = JSON.parse(d);

        var dt = normalizeSparql(p);
        var ix = makeIndex(dt);
        
        var result = {v: v, data: dt, index: ix };

        fs.writeFileSync(__dirname + "/units.json", JSON.stringify(result, null, 2));
        cb(result);
    } catch(e) {
        androidStudioLogging.warning("Couldn't update unit-conversion tables from Wikidata: " + e);
    }
}

function makeIndex(data) {
    var ls = Object.values(data);
    var index = {};

    ls.forEach(x=>{
        addToIndex(index, x.unit, x.key);
        x.abbs.forEach(y=>{
            addToIndex(index, y, x.key);
        });
        caseByCaseIndexUpdate(index, x);
    });

    return index;
}

function caseByCaseIndexUpdate(index, row) {
    if(row.unit == "degree") {
        addToIndex(index, "degs", row.key);
        addToIndex(index, "deg", row.key);
    }
}

function addToIndex(index, key, name) {
    if(index[key] == undefined) index[key] = [];
    index[key].push(name);
}

function normalizeSparql(result) {
    var dataArr = result.results.bindings;

    var dataObj = {};

    var units = Array.from(new Set(dataArr.map(x=>getUnitId(x))));

    units.forEach(x=>assignNormalRows(
        dataObj,
        dataArr.filter(y=>getUnitId(y)== x)
    ));

    return dataObj;
}

function getUnitId(row) {
    return row.itemLabel.value.trim() + " (" 
    + (row.convAppLabel ? row.convAppLabel.value + " -- " : "")
    + row.conv.value + " " + row.convUnitLabel.value + ")";
}

function assignNormalRows(o, rows) {
    var unitName = getUnitId(rows[0]);

    var abbreviations = Array.from(new Set(rows.map(x=>x.sym.value)));


    o[unitName] = {
        key: unitName,
        unit: rows[0].itemLabel.value,
        dimension: parseBaseUnitDimension(rows[0].convUnitLabel.value),
        abbs: abbreviations,
        conversionFactors: +rows[0].conv.value
    }
    
}

function parseBaseUnitDimension(u) {
    if(u == "1") return "1";

    var powerNormalizedU = u
        .toLowerCase()
        .replace(/\bto the (\w+) power\b/g, "to the power of $1")
        .replace(/(\w+) to the power of (\w+)/g, "$2tic $1");

    //start as a unitless value (all powers 0)
    var resultPowers = {
        L:0,
        T:0,
        M:0,
        I:0,
        C:0,
        J:0,
        A:0,
        N:0
    };

    var words = powerNormalizedU.split(/\W+/);

    var isInDenominator = 1, nextExp = 1;

    for(var i = 0; i < words.length; i++) {
        var w = words[i];

        if(w == "difference") continue //ignore "difference" it doesn't affect the dimension. the temperature just uses it bc it's not a direct coef.
        if(w == "per" || w == "reciprocal") isInDenominator = -1;
        else if(isBaseUnit(w)) addBaseUnitPhysicalPowers(w, resultPowers, nextExp * isInDenominator)
        
        nextExp = parseExp(w);
    }

    return serializePowers(resultPowers);
}

function serializePowers(pows) {
    var ents = Object.entries(pows);

    var r = [];

    ents.forEach(x=> {
        if(x[1] != 0) {
            if(x[1] == 1) r.push(x[0]);
            else r.push(x[0] + "^" + x[1]);
        }
    });

    if(r.length == 0) return "1";
    else return r.join(" ");
}

function isBaseUnit(u) {
    return getPhysicalPowers(u) != undefined;
}

function addBaseUnitPhysicalPowers(unit, r, exponent) {
    var pows = getPhysicalPowers(unit);

    pows.forEach(x=>{
        r[x[0]] += x[1] * exponent;
    });
}


function getPhysicalPowers(unit) {
    return ({
        kelvin: [["C",1]],
        metre: [["L",1]],
        kilogram: [["M",1]],
        lumen: [["J",1]],
        candela: [["J",1]],
        mole: [["N",1]],
        radian: [["A",1]],
        ampere: [["I",1]],
        nanometre: [["L",1]],
        second: [["T",1]],

        steradian: [["A",2]],
        millilitre: [["L",3]],

        hertz: [["T", -1]],
        becquerel: [["T", -1]],

        joule: [
            ["L",2], ["M",1],["T",-2]
        ],
        henry: [
            ["L",2], ["M",1], ["T",-2], ["I",-2]
        ],
        watt: [
            ["L",2], ["M",1], ["T",-3]
        ],
        siemens: [
            ["L",-2], ["M",-1], ["T",3], ["I",2]
        ],
        katal: [
            ["T",-1], ["N", 1]
        ],
        pascal: [
            ["L",-1], ["M",1], ["T",-2]
        ],
        volt: [
            ["L",2], ["M",1], ["T",-3], ["I",-1]
        ],
        weber: [
            ["L",2], ["M",1], ["T",-2], ["I",-1]
        ],
        newton: [
            ["L",1], ["M",1], ["T",-2]
        ],
        lux: [
            ["L",-2], ["J",1]
        ],
        ohm: [
            ["L",2], ["M",1], ["T",-3], ["I",-2]
        ],
        farad: [
            ["L",-2], ["M",-1], ["T",4], ["I",2]
        ],
        sievert: [
            ["L",2], ["T",-2]
        ],
        tesla: [
            ["M",1], ["T",-2], ["I",-1]
        ],
        gray: [
            ["L",2], ["T",-2]
        ],
        coulomb: [
            ["T",1], ["I",1]
        ]


    })[unit];
}

function parseExp(word) {
    if(word.startsWith("square")) return 2;
    if(word.startsWith("cub") || /$th(ir|ee)/.test(word)) return 3;
    if(word.startsWith("quar") || word.startsWith("four")) return 4;
    if(/$fi(ft|ve)/.test(word) || word.startsWith("quin")) return 5;
    if(/$s.xtic/.test(word)) return 6;
    if(word.startsWith("sev")) return 7;

    //whyyyy
    //solely for volt-ampere-reactive second
    if(word == "reactive") return -2;

    return 1;
}

function allRowsHaveConversionUse(rows) {
    return rows.find(x=>x.convAppLabel == undefined) == undefined;
}

function averageNumStrs(n) {
    var t = 0;
    n.forEach(x=>t += (+n) || 0);
    return t / n.length;
}