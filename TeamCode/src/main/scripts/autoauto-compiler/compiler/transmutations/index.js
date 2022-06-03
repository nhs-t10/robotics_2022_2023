var fs = require("fs");
const path = require("path");
const folderScanner = require("../folder-scanner");

var memoizeParsedAliases = {};

var preprocessTransmutations = [];
var transmutations = {};
var postprocessTransmutations = [];

module.exports = {
    /**
     * 
     * @param {string} s 
     * @returns {SerializableTransmutationInstance[]}
     */
    expandTasks: function(taskSpec, file) {
        var tasks = memoizedParseSpecExpandAliases(taskSpec, file);
        insertDeps(tasks, undefined, file);
        
        return idsToTras(tasks);
    },
    loadTaskList: async function() {
        //TODO: implement `loadTransmutations()` asynchronously. See the file-scanner for example.
        await loadTransmutations(__dirname);
    },
    getPostProcessTransmutations: function() {
        return postprocessTransmutations;
    },
    getPreProcessTransmutations: function () {
        return preprocessTransmutations;
    }
};

function idsToTras(ids) {
    return ids.map(x=>{
        var f = {};
        Object.assign(f, transmutations[x.id]);
        f.isDependency = x.dep;
        return f;
    });
}

function memoizedParseSpecExpandAliases(sp, file) {
    
    var t = JSON.stringify(sp);
    
    var cVal = memoizeParsedAliases[t];
    
    if(!cVal) {
        cVal = parseSpecExpandAliases(sp, file);
        memoizeParsedAliases[t] = cVal;
    }
    return cVal;
}

/**
     * @param {string|string[]} s
     * @returns {TransmutationTask[]}
     */
function parseSpecExpandAliases(sp, file) {
    if(typeof sp === "string") sp = sp.split(/ +/);

    var r = [];
    sp.forEach(x => {
        x = x.trim();

        var expanded = expandAlias(x, file);
        
        r = r.concat(expanded);
    });
    
    return r;
}

function insertDeps(insertInto, findDepsIn, file) {
    if(findDepsIn === undefined) findDepsIn = insertInto;
    
    for(var i = 0; i < findDepsIn.length; i++) {
        var req = transmutations[findDepsIn[i].id].requires;
        var reqExpanded = memoizedParseSpecExpandAliases(req, file);
        
        var intoIndex = insertInto.findIndex(x=>x.id == findDepsIn[i].id);
        
        for(var j = 0; j < reqExpanded.length; j++) {
            var x = reqExpanded[j].id;
            if(!insertInto.find(z=>z.id == x)) {
                insertInto.splice(intoIndex,0,{id:x,dep:true});
                intoIndex++;
            }
        }
        insertDeps(insertInto, reqExpanded, file);
    }
}

function expandAlias(spk, file) {
    spk = spk.trim();
    
    var category = spk.startsWith(":");
    spk = spk.replace(":","");

    var keys = Object.keys(transmutations);
    var rgxp;
    try {
        rgxp = new RegExp("^" + spk.replace(/\*/g, ".*") + "$");
    } catch(e) {
        throw `Bad task specifier ${JSON.stringify(spk)} in ${file}`;
    }
    
    var globspanded = keys.filter(x => {
        if(category) return rgxp.test(transmutations[x].type);
        else return rgxp.test(x);
    });
    

    return globspanded.map(x => {
        var e = transmutations[x];
        if (!e) throw "No such transmutation `" + x + "`";
        
        if (e.type == "alias") return e.aliasesTo.split(" ").map(x => expandAlias(x, file));
        else return {id: e.id};
    }).flat(2);
}

async function loadTransmutations(dirname) {
    var files = folderScanner(dirname, ".transmute-meta.js");
    
    while(true) {
        var f = await files.next();
        if(f.done) break;
        else loadTransmutation(f.value.replace(".transmute-meta.js", ".js"), f.value);
    }
}

function loadTransmutation(sourceFile, metaFile) {
    var meta = require(metaFile);
    meta.sourceFile = sourceFile;
    
    if(meta.type == "codebase_postprocess") {
        postprocessTransmutations.push(meta);
    } else if(meta.type == "codebase_preprocess") {
        preprocessTransmutations.push(meta);
    } else {
        if (transmutations[meta.id]) throw "The transmutation `" + meta.id + "` is already registered!";
        else transmutations[meta.id] = meta;
    }
}

/**
 * @typedef {object} TransmutationMetadata
 * @property {string[]} requires
 * @property {string} id
 * @property {"output"|"transformation"|"input"|"information"|"check","codebase_postprocess"|"codebase_preprocess"|"alias"} type
 * @property {string[]?} readsFiles An optional array of files which this transmutation reads. This is important for caching.
 */

/**
 * @typedef {TransmutationMetadata} Transmutation
 * @property {string} sourceFile 
 */


/**
 * @typedef {object} SerializableTransmutationInstance
 * @property {string[]} requires
 * @property {string} id
 * @property {"output"|"transformation"|"input"|"information"|"check","codebase_postprocess"|"codebase_postprocess"|"alias"} type
 * @property {string[]?} readsFiles
 * @property {boolean} isDependency
 * @property {string} sourceFile
 */

/**
 * @callback TransmutationFunction
 * @param {TransmutateContext} context
 * @param {TransmutateContext[]?} contexts
 * @returns {TransmutateContext?}
 */

/**
 * @typedef {object} TransmutateContext
 * @property {"pass"|"fail"} status
 * @property {*} output
 * @property {Object.<string, *>} inputs
 * @property {*} lastInput
 *
 * @property {string} fileContentText
 * @property {string} sourceDir
 * @property {string} sourceBaseFileName
 * @property {string} sourceFullFileName
 *
 * @property {string} resultDir
 * @property {string} resultFullFileName
 * @property {string} resultBaseFileName
 *
 * @property {string} resultRoot
 * @property {string} sourceRoot
 * @property {string} assetsRoot
 * @property {string} testRoot
 * 
 * @property {Object.<string, string>} writtenFiles
 * @property {string[]} readsAllFiles
 *
 * @property {object} fileFrontmatter
 * @property {SerializableTransmutationInstance[]} transmutations
 * 
 * @property {string} cacheKey
 */