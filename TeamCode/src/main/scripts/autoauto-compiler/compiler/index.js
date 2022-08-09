"use strict";

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
const TEST_FILES_DIRECTORY = BUILD_ROOT_DIRS.test;

module.exports = (async function main() {
    const startTimeMs = Date.now(); 
    
    await transmutations.loadTaskList();
    const fileCount = await compileAllFromSourceDirectory();

    androidStudioLogging.printAppendixes();
    
    androidStudioLogging.printTypeCounts();
    
    androidStudioLogging.printTimingInformation(fileCount, Date.now() - startTimeMs);
});

async function compileAllFromSourceDirectory() {
    const compilerWorkers = makeWorkersPool();

    const preprocessInputs = {};
    const codebaseTransmutationWrites = {};
    await evaluateCodebaseTasks([], transmutations.getPreProcessTransmutations(), preprocessInputs, codebaseTransmutationWrites);

    const environmentHash = makeEnvironmentHash(CACHE_VERSION, preprocessInputs, process.argv);

    //the folderScanner will give once for each file.
    //this way, we don't have to wait for ALL filenames in order to start compiling.
    //it starts after the first one!
    const aaFiles = folderScanner(SRC_DIRECTORY, ".autoauto");
    const jobPromises = [];

    for await (const file of aaFiles) {
        jobPromises.push(
            makeContextAndCompileFile(file, compilerWorkers, preprocessInputs, environmentHash)
        );
    }
    compilerWorkers.finishGivingJobs();

    const compilationResults = await Promise.all(jobPromises);

    await evaluateCodebaseTasks(compilationResults, transmutations.getPostProcessTransmutations(), {}, codebaseTransmutationWrites);
    writeWrittenFiles({ writtenFiles: codebaseTransmutationWrites });

    compilerWorkers.close();
    
    return jobPromises.length;
}

/**
 * 
 * @param {string} filename 
 * @param {import("./workers-pool").workerPool} compilerWorkers
 * @param {Object.<string, *>} preprocessInputs
 * @param {string} environmentHash 
 * @returns {Promise<import("./worker").MaybeCompilation>}
 */
function makeContextAndCompileFile(filename, compilerWorkers, preprocessInputs, environmentHash) {
    const fileContext = makeFileContext(filename, preprocessInputs, environmentHash);
    const cacheEntry = getCacheEntry(fileContext);

    return new Promise(function (resolve, reject) {
        if (cacheEntry) {
            const cacheContext = cacheEntry.fileContext;

            androidStudioLogging.sendMessages(cacheEntry.log);
            compilerWorkers.addFinishedJobFromCache(cacheContext);
            writeWrittenFiles(cacheContext);
            resolve(cacheEntry);
        } else {
            compilerWorkers.giveJob(fileContext, function (run) {              
                if(run.success === "SUCCESS") {
                    saveCacheEntry(run);
                    
                    androidStudioLogging.sendMessages(run.log);
                    writeWrittenFiles(run.fileContext);
                    resolve(run);
                } else {
                    if(run.log !== undefined) androidStudioLogging.sendMessages(run.log);
                    if(run.error) androidStudioLogging.sendInternalError(run.error);
                    resolve(run);
                }
            });
        }
    });
}

/**
 * 
 * @param {import("./worker").MaybeCompilationSucceeded} finishedRun 
 */
function saveCacheEntry(finishedRun) {
    if (commandLineInterface["no-cache-save"] == false) {
        cache.save(mFileCacheKey(finishedRun.fileContext), {
            success: "SUCCESS",
            subkey: finishedRun.fileContext.cacheKey,
            fileContext: finishedRun.fileContext,
            log: finishedRun.log
        });
    }
}

/**
 * @typedef {import("./worker").MaybeCompilationSucceeded & ({subkey: string})} CacheEntry
 */

/**
 * 
 * @param {import("./transmutations").TransmutateContext} fileContext
 * @returns {CacheEntry?}
 */
function getCacheEntry(fileContext) {
    if (commandLineInterface["no-cache"]) return null;

    /** @type {CacheEntry} */
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

/**
 * 
 * @param {import("./worker").MaybeCompilation[]} allFileContexts 
 * @param {import("./transmutations").SerializableTransmutationInstance[]} codebaseTasks 
 * @param {Object.<string, *>} codebaseInputs 
 * @param {Object.<string, string | Buffer>} codebaseTransmutationWrites 
 */
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

/**
 * 
 * @param {Object.<string, *>} codebaseTransmutationWrites 
 * @returns {import("./transmutations").CodebaseContext}
 */
function makeCodebaseContext(codebaseTransmutationWrites) {
    return {
        output: undefined,
        writtenFiles: codebaseTransmutationWrites,
        resultRoot: COMPILED_RESULT_DIRECTORY,
        assetsRoot: ASSETS_DIRECTORY,
        sourceRoot: SRC_DIRECTORY,
        testRoot: TEST_FILES_DIRECTORY
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
        testRoot: TEST_FILES_DIRECTORY,
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

/**
 * 
 * @param {import("./transmutations").TransmutateContext} fileContext 
 */
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
