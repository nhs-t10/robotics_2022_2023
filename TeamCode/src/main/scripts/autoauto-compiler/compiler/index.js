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
const { sha, shaJSON } = require("../../script-helpers/sha-string");
const { finishCacheKeys, makeCacheKey } = require("./cache-keys");

const BUILD_ROOT_DIRS = (require("./get-build-root"))();

const SRC_DIRECTORIES = BUILD_ROOT_DIRS.src;
const COMPILED_RESULT_DIRECTORY = BUILD_ROOT_DIRS.gen;
const ASSETS_DIRECTORY = BUILD_ROOT_DIRS.asset;
const TEST_FILES_DIRECTORY = BUILD_ROOT_DIRS.test;

module.exports = (async function main() {
    const startTimeMs = Date.now(); 
    
    await transmutations.loadTaskList();
    const fileCount = await compileAllWithPool();

    androidStudioLogging.printAppendixes();
    
    androidStudioLogging.printTypeCounts();
    
    androidStudioLogging.printTimingInformation(fileCount, Date.now() - startTimeMs);
});

async function compileAllWithPool() {
    const pool = makeWorkersPool();
    const fileCount = await compileAllFromSourceDirectory(pool);
    pool.close();
    return fileCount;
}

async function compileAllFromSourceDirectory(compilerWorkers) {
    const preprocessInputs = {};
    const codebaseTransmutationWrites = {};
    
    const preProcessSuccessful = 
        await evaluateCodebaseTasks([], transmutations.getPreProcessTransmutations(), preprocessInputs, codebaseTransmutationWrites);
        
    if(!preProcessSuccessful) return 0;

    const environmentHash = makeEnvironmentHash(CACHE_VERSION, preprocessInputs, process.argv);

    //the folderScanner will give once for each file.
    //this way, we don't have to wait for ALL filenames in order to start compiling.
    //it starts after the first one!
    const aaFiles = folderScanner(SRC_DIRECTORIES, ".autoauto", null, true);
    const jobPromises = [];

    for await (const file of aaFiles) {
        jobPromises.push(
            makeContextAndCompileFile(file[1], compilerWorkers, preprocessInputs, environmentHash, file[0])
        );
    }
    finishCacheKeys();
    await compilerWorkers.finishGivingJobs();

    const compilationResults = await Promise.all(jobPromises);

    const postProcessSuccessful =
        await evaluateCodebaseTasks(compilationResults, transmutations.getPostProcessTransmutations(), {}, codebaseTransmutationWrites);
    if (!postProcessSuccessful) return 0;
    
    writeWrittenFiles({ writtenFiles: codebaseTransmutationWrites });
    
    return jobPromises.length;
}

/**
 * 
 * @param {string} filename 
 * @param {import("./workers-pool").workerPool} compilerWorkers
 * @param {Object.<string, *>} preprocessInputs
 * @param {string} environmentHash 
 * @param {string} rootDirectory
 * @returns {Promise<import("./worker").MaybeCompilation>}
 */
function makeContextAndCompileFile(filename, compilerWorkers, preprocessInputs, environmentHash, rootDirectory) {
    
    return new Promise(async function (resolve, reject) {
        const fileContext = makeFileContext(filename, preprocessInputs, rootDirectory);
        const cacheEntry = await getCacheEntry(fileContext, environmentHash);
        
        if (cacheEntry) {
            const cacheContext = cacheEntry.fileContext;

            androidStudioLogging.sendMessages(cacheEntry.log);
            compilerWorkers.addFinishedJobFromCache(cacheContext, cacheEntry.log);
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
 * @param {string} environmentHash
 * @returns {Promise<CacheEntry?>}
 */
async function getCacheEntry(fileContext, environmentHash) {
    if (commandLineInterface["no-cache"]) return null;

    /** @type {CacheEntry} */
    const cacheEntry = cache.get(mFileCacheKey(fileContext), null);

    //if the cache entry exists, then use its fileContext for the cache key. Otherwise, use the clean one we were given.
    const freshKey = await makeCacheKey(cacheEntry ? cacheEntry.fileContext : fileContext, environmentHash);
    
    if (cacheEntry != null && cacheEntry.subkey == freshKey) {
        fileContext.cacheKey = freshKey;
        cacheEntry.fileContext.cacheKey = freshKey;
        return cacheEntry;
    } else {
        return null;
    }
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
 * @returns {Promise<boolean>} `true` if all tasks ran properly 
 */
async function evaluateCodebaseTasks(allFileContexts, codebaseTasks, codebaseInputs, codebaseTransmutationWrites) {
    for (const transmut of codebaseTasks) {
        const o = makeCodebaseContext(codebaseTransmutationWrites);
        const mutFunc = require(transmut.sourceFile);
        await mutFunc(o, allFileContexts);
        
        if (o.status == "pass") codebaseInputs[transmut.id] = o.output; 
        else return false;
    }
    
    return true;
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
        sourceRoots: SRC_DIRECTORIES,
        testRoot: TEST_FILES_DIRECTORY
    }
}

function makeFileContext(file, preprocessInputs, rootDirectory) {

    const resultFile = getResultFor(file, rootDirectory);
    const fileContent = fs.readFileSync(file).toString();
    const frontmatter = loadFrontmatter(fileContent);

    const tPath = transmutations.expandTasks(frontmatter.compilerMode || "default", file);

    const ctx = {
        sourceBaseFileName: path.basename(file),
        sourceDir: path.dirname(file),
        sourceFullFileName: file,
        sourceRoots: SRC_DIRECTORIES,
        resultBaseFileName: path.basename(resultFile),
        resultDir: path.dirname(resultFile),
        resultFullFileName: resultFile,
        resultRoot: COMPILED_RESULT_DIRECTORY,
        assetsRoot: ASSETS_DIRECTORY,
        testRoot: TEST_FILES_DIRECTORY,
        fileFrontmatter: frontmatter,
        isLibrary: frontmatter.compilerMode == "lib",
        fileContentText: fileContent,
        lastInput: fileContent,
        inputs: {},
        cacheKey: undefined,
        writtenFiles: {},
        
        dependsOn: {},

        transmutations: tPath,
        readsAllFiles: tPath.map(x => x.readsFiles || []).flat()
    };

    Object.assign(ctx.inputs, preprocessInputs);

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
        t.push(shaJSON(object[key]));
    }
    return t.join("");
}

function getResultFor(filename, root) {
    const folder = path.dirname(filename);

    const packageFolder = folder
        .replace(root, "").toLowerCase();

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