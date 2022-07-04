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
const loadFrontmatter = require("./frontmatter-parser");

const BUILD_ROOT_DIRS = (require("./get-build-root"))();

const SRC_DIRECTORY = BUILD_ROOT_DIRS.src
const COMPILED_RESULT_DIRECTORY = BUILD_ROOT_DIRS.gen;
const ASSETS_DIRECTORY = BUILD_ROOT_DIRS.asset;

module.exports = (async function main() {
    await transmutations.loadTaskList();
    await compileAllFromSourceDirectory();

    androidStudioLogging.printTypeCounts();
});

async function compileAllFromSourceDirectory() {
    const compilerWorkers = makeWorkersPool();
    const autoautoFileContexts = [];

    const preprocessInputs = {};
    const codebaseTransmutationWrites = {};
    await evaluateCodebaseTasks(autoautoFileContexts, transmutations.getPreProcessTransmutations(), preprocessInputs, codebaseTransmutationWrites);

    const environmentHash = makeEnvironmentHash(CACHE_VERSION, preprocessInputs, process.argv);

    //the folderScanner will give once for each file.
    //this way, we don't have to wait for ALL filenames in order to start compiling.
    //it starts after the first one!
    const aaFiles = folderScanner(SRC_DIRECTORY, ".autoauto");
    const jobPromises = [];

    for await (const file of aaFiles) {
        jobPromises.push(
            makeContextAndCompileFile(file, compilerWorkers, autoautoFileContexts, preprocessInputs, environmentHash)
        );
    }

    await Promise.all(jobPromises);

    await evaluateCodebaseTasks(autoautoFileContexts, transmutations.getPostProcessTransmutations(), {}, codebaseTransmutationWrites);
    writeWrittenFiles({ writtenFiles: codebaseTransmutationWrites });

    compilerWorkers.close();
}

function makeContextAndCompileFile(filename, compilerWorkers, autoautoFileContexts, preprocessInputs, environmentHash) {
    const fileContext = makeFileContext(filename, preprocessInputs, environmentHash);
    const cacheEntry = getCacheEntry(fileContext);

    return new Promise(function (resolve, reject) {
        if (cacheEntry) {
            androidStudioLogging.sendMessages(cacheEntry.log);
            writeAndCallback(cacheEntry.data, autoautoFileContexts, resolve);
            compilerWorkers.addFinishedJobFromCache(fileContext);
        } else {
            compilerWorkers.giveJob(fileContext, function (/** @type {import("./worker").MaybeCompilation} */run) {                
                if(run.success === "SUCCESS") {
                    saveCacheEntry(run);
                    androidStudioLogging.sendMessages(run.log);
                    writeAndCallback(run.fileContext, autoautoFileContexts, resolve);
                } else {
                    noteFatalError(run);
                }
            });
        }
    });
}

/**
 * 
 * @param {import("./worker").MaybeCompilationFailed} failedRun
 */
function noteFatalError(failedRun) {
    if(failedRun.error instanceof Error) {
        androidStudioLogging.sendTreeLocationMessage({
            kind: "ERROR",
            text: "Internal Compiler Error",
            original: `There was an internal error. This file will be skipped, but others will still be compiled.\n` +
                `Please contact these people in this order: \n` +
                `1) Connor\n` +
                `2) Chloe\n` +
                `\n` +
                `The stack of the error is below:\n` +
                failedRun.error.message + "\n" + failedRun.error.stack
        }, undefined, "ERROR", true);
    } else {
        androidStudioLogging.sendTreeLocationMessage(failedRun.error, undefined, "ERROR", true);
    }
}

/**
 * 
 * @param {import("./worker").MaybeCompilationSucceeded} finishedRun 
 */
function saveCacheEntry(finishedRun) {
    if (commandLineInterface["no-cache-save"] == false) {
        cache.save(mFileCacheKey(finishedRun.fileContext), {
            subkey: finishedRun.fileContext.cacheKey,
            data: finishedRun.fileContext,
            log: finishedRun.log
        });
    }
}

/**
 * @typedef {object} CacheEntry
 * @property {string} subkey
 * @property {import("./transmutations").TransmutateContext} data
 * @property {androidStudioLoggingMessage[]} log
 */

/**
 * 
 * @param {import("./transmutations").TransmutateContext} fileContext
 * @returns {CacheEntry?}
 */
function getCacheEntry(fileContext) {
    if (commandLineInterface["no-cache"]) return null;

    const cacheEntry = cache.get(mFileCacheKey(fileContext), null);

    if (cacheEntry != null && cacheEntry.subkey == fileContext.cacheKey) return cacheEntry;
    else return null;
}

/**
 * 
 * @param {import("./transmutations").TransmutateContext} fileContext
 * @returns {string}
 */
function mFileCacheKey(fileContext) {
    return "autoauto compiler file cache " + fileContext.sourceFullFileName;
}

function writeAndCallback(finishedFileContext, autoautoFileContexts, cb) {
    autoautoFileContexts.push(finishedFileContext);
    writeWrittenFiles(finishedFileContext);
    cb(finishedFileContext);
}

async function evaluateCodebaseTasks(allFileContexts, codebaseTasks, codebaseInputs, codebaseTransmutationWrites) {
    for (const transmut of codebaseTasks) {
        const o = makeCodebaseContext(codebaseTransmutationWrites);
        const mutFunc = require(transmut.sourceFile);
        await mutFunc(o, allFileContexts);
        codebaseInputs[transmut.id] = o.output;
    }
}

function sha(s) {
    return crypto.createHash("sha256").update(s).digest("hex");
}

function makeCodebaseContext(codebaseTransmutationWrites) {
    return {
        output: undefined,
        writtenFiles: codebaseTransmutationWrites,
        resultRoot: COMPILED_RESULT_DIRECTORY,
        assetsRoot: ASSETS_DIRECTORY,
        sourceRoot: SRC_DIRECTORY
    }
}

function makeFileContext(file, preprocessInputs, environmentHash) {

    const resultFile = getResultFor(file);
    const fileContent = fs.readFileSync(file).toString();
    const frontmatter = loadFrontmatter(fileContent);

    const tPath = transmutations.expandTasks(frontmatter.compilerMode || "default", file);

    const ctx = {
        sourceBaseFileName: path.basename(file),
        sourceDir: path.dirname(file),
        sourceFullFileName: file,
        sourceRoot: SRC_DIRECTORY,
        resultBaseFileName: path.basename(resultFile),
        resultDir: path.dirname(resultFile),
        resultFullFileName: resultFile,
        resultRoot: COMPILED_RESULT_DIRECTORY,
        assetsRoot: ASSETS_DIRECTORY,
        fileFrontmatter: frontmatter,
        fileContentText: fileContent,
        lastInput: fileContent,
        inputs: {},
        cacheKey: undefined,
        writtenFiles: {},

        transmutations: tPath,
        readsAllFiles: tPath.map(x => x.readsFiles || []).flat()
    };

    Object.assign(ctx.inputs, preprocessInputs);
    ctx.cacheKey = makeCacheKey(ctx, environmentHash);

    return ctx;
}

function writeWrittenFiles(fileContext) {
    for (const filename in fileContext.writtenFiles) {
        const content = fileContext.writtenFiles[filename];
        if (typeof content !== "boolean") {
            safeFsUtils.safeWriteFileEventually(filename, content);
        }
    }
}

function makeEnvironmentHash(cacheVersion, preprocessInputs, argv) {
    return cacheVersion + "\0" + keyJsonHash(preprocessInputs) + "\0" + argv.join(" ");
}

function keyJsonHash(object) {
    let t = [];
    for (const key in object) {
        t.push(sha(JSON.stringify(object[key])));
    }
    return t.join("");
}

/**
 * 
 * @param {import("./transmutations").TransmutateContext} fileContext 
 */
function makeCacheKey(fileContext, environmentHash) {
    const readFileShas = fileContext.readsAllFiles.map(x => sha(safeFsUtils.cachedSafeReadFile(x))).join("\t");
    const transmutationIdList = fileContext.transmutations.map(x => x.id).join("\t");

    const keyDataToSha = [environmentHash, readFileShas,
        fileContext.sourceFullFileName, fileContext.fileContentText, transmutationIdList];

    return sha(keyDataToSha.join("\0"));
}

function getResultFor(filename) {
    const folder = path.dirname(filename);

    const packageFolder = folder
        .replace(SRC_DIRECTORY, "").toLowerCase();

    const javaFileName = jClassIfy(filename) + ".java";

    return path.join(COMPILED_RESULT_DIRECTORY, packageFolder, javaFileName);
}

function jClassIfy(str) {
    const p = str.split(/\/|\\/);
    const s = p[p.length - 1].split(".")[0];
    return s.split("-").map(x => capitalize(x)).join("");
}
function capitalize(str) {
    return str[0].toUpperCase() + str.substring(1);
}
