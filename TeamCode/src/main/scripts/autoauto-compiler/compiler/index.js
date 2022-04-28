const fs = require("fs");
const path = require("path");
const crypto = require("crypto");

const androidStudioLogging = require("../../script-helpers/android-studio-logging");

const transmutations = require("./transmutations");
const cache = require("../../cache");
const CACHE_VERSION = require("../config").CACHE_VERSION;
const commandLineInterface = require("../../command-line-interface");
const safeFsUtils = require("../../script-helpers/safe-fs-utils");
const makeWorkersPool = require("./workers-pool");
const folderScanner = require("./folder-scanner");

var SRC_DIRECTORY = __dirname.substring(0 , __dirname.indexOf("src") + "src".length + 1);
var COMPILED_RESULT_DIRECTORY = path.join(SRC_DIRECTORY, "../gen/org/firstinspires/ftc/teamcode/__compiledautoauto");


module.exports = (async function main() {
    await transmutations.loadTaskList();
    await compileAllFromSourceDirectory();
    
    androidStudioLogging.printTypeCounts();
});

async function compileAllFromSourceDirectory() {
    const compilerWorkers = makeWorkersPool();
    const autoautoFileContexts = [];
    
    var preprocessInputs = {};
    await evaluateCodebaseTasks(autoautoFileContexts, transmutations.getPreProcessTransmutations(), preprocessInputs);

    //this callback will call once for each file.
    //this way, we don't have to wait for ALL filenames in order to start compiling.
    //it starts after the first one!
    var aaFiles = folderScanner(SRC_DIRECTORY, ".autoauto");
    var jobPromises = [];
    
    while(true) {
        var next = await aaFiles.next();
        if(next.done) break;
        
        jobPromises.push(
            makeContextAndCompileFile(next.value, compilerWorkers, autoautoFileContexts, preprocessInputs)
        );
    }
    
    await Promise.all(jobPromises);

    await evaluateCodebaseTasks(autoautoFileContexts, transmutations.getPostProcessTransmutations(), {});
    compilerWorkers.close();
}

function makeContextAndCompileFile(filename, compilerWorkers, autoautoFileContexts, preprocessInputs) {
    var fileContext = makeFileContext(filename, preprocessInputs);
    var cacheEntry = getCacheEntry(fileContext);

    return new Promise(function(resolve, reject) {
        if(cacheEntry) {
            androidStudioLogging.sendMessages(cacheEntry.log);
            writeAndCallback(cacheEntry.data, autoautoFileContexts, resolve);
        } else {
            compilerWorkers.giveJob(fileContext, function(run) {
                saveCacheEntry(run);
                androidStudioLogging.sendMessages(run.log);
                writeAndCallback(run.fileContext, autoautoFileContexts, resolve);
            });
        }
    });
}

function saveCacheEntry(finishedRun) {
    if(finishedRun.success) {
        cache.save(mFileCacheKey(finishedRun.fileContext), {
            subkey: finishedRun.fileContext.cacheKey,
            data: finishedRun.fileContext,
            log: finishedRun.log
        });
    }
}

function getCacheEntry(fileContext) {
    if(commandLineInterface["no-cache"]) return false;

    var cacheEntry = cache.get(mFileCacheKey(fileContext), false);

    if(cacheEntry.subkey == fileContext.cacheKey) return cacheEntry;
    else return false;
}

function mFileCacheKey(fileContext) {
    return "autoauto compiler file cache " + fileContext.sourceFullFileName;
}

function writeAndCallback(finishedFileContext, autoautoFileContexts, cb) {
    autoautoFileContexts.push(finishedFileContext);
    writeWrittenFiles(finishedFileContext);
    cb(finishedFileContext);
}

async function evaluateCodebaseTasks(allFileContexts, codebaseTasks, codebaseInputs) {
    for(var transmut of codebaseTasks) {
        var o = { output: undefined };
        var mutFunc = require(transmut.sourceFile);
        await mutFunc(o, allFileContexts);
        codebaseInputs[transmut.id] = o.output;
    }
}

function sha(s) {
    return crypto.createHash("sha256").update(s).digest("hex");
}

function makeFileContext(file, preprocessInputs) {
        
    var resultFile = getResultFor(file);
    var fileContent = fs.readFileSync(file).toString();
    var frontmatter = loadFrontmatter(fileContent);

    var tPath = transmutations.expandTasks(frontmatter.compilerMode || "default");
    
    var ctx = {
        sourceBaseFileName: path.basename(file),
        sourceDir: path.dirname(file),
        sourceFullFileName: file,
        sourceRoot: SRC_DIRECTORY,
        resultBaseFileName: path.basename(resultFile),
        resultDir: path.dirname(resultFile),
        resultFullFileName: resultFile,
        resultRoot: COMPILED_RESULT_DIRECTORY,
        fileFrontmatter: frontmatter,
        fileContentText: fileContent,
        lastInput: fileContent,
        inputs: {},
        cacheKey: undefined,
        writtenFiles: {},

        transmutations: tPath,
        readsAllFiles: tPath.map(x=>x.readsFiles || []).flat()
    };
    
    Object.assign(ctx.inputs, preprocessInputs);
    ctx.cacheKey = makeCacheKey(ctx, preprocessInputs);

    return ctx;
}

function writeWrittenFiles(fileContext) {
    for(var filename in fileContext.writtenFiles) {
        safeFsUtils.safeWriteFileEventually(filename, fileContext.writtenFiles[filename]);
    }
}

/**
 * 
 * @param {import("./transmutations").TransmutateContext} fileContext 
 */
function makeCacheKey(fileContext, preprocessInputs) {
    
    var preprocessInputSerial = "";
    for(var ppI in preprocessInputs) {
        preprocessInputSerial += sha(JSON.stringify(preprocessInputs[ppI]));
    }
    
    var readFileShas = fileContext.readsAllFiles.map(x=>sha(safeFsUtils.cachedSafeReadFile(x))).join("\t");
    var transmutationIdList = fileContext.transmutations.map(x=>x.id).join("\t");

    var keyDataToSha = [CACHE_VERSION, preprocessInputSerial, readFileShas, 
        fileContext.sourceFullFileName, fileContext.fileContentText, transmutationIdList];

    return sha(keyDataToSha.join("\0"));
}

function getResultFor(filename) {
    var folder = path.dirname(filename);
    
    var packageFolder = folder
        .replace(SRC_DIRECTORY, "").toLowerCase();
    packageFolder = packageFolder.substring(packageFolder.indexOf("teamcode"));
    
    var javaFileName = jClassIfy(filename) + ".java";
        
    return path.join(COMPILED_RESULT_DIRECTORY, packageFolder, javaFileName);
}

function jClassIfy(str) {
    var p = str.split(/\/|\\/);
    var s = p[p.length - 1].split(".")[0];
    return s.split("-").map(x=>capitalize(x)).join("");
}
function capitalize(str) {
    return str[0].toUpperCase() + str.substring(1);
}

function loadFrontmatter(fCont) {
    
    try {
        var fmI = fCont.indexOf("$");
        if(fmI == -1) return {};
        
        var fmE = fCont.indexOf("$", fmI + 1);
        if(fmE == -1) return {};
        
        var fmPar = (new Function(`return ({${fCont.substring(fmI + 1, fmE)}})`))();
        
        return fmPar;
        
    } catch(e) {
        return {};
    }
}
