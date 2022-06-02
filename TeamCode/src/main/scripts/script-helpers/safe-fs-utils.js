var fs = require("fs");
var path = require("path");
const androidStudioLogging = require("./android-studio-logging");
const cachedFs = require("./cached-fs");


const cachedGitDirectory = getGitRootDirectory();
const cachedGradleRootDirectory = getGradleRootDirectory();

module.exports = {
    safeWriteFile: safeWriteFile,
    createDirectoryIfNotExist: createDirectoryIfNotExist,
    cleanDirectory: cleanDirectory,
    addToGitignore: addToGitignore,
    safeReadFile: safeReadFile,
    cachedSafeReadFile: cachedSafeReadFile,
    safeWriteFileEventually: safeWriteFileEventually,
    deleteIfExists: deleteIfExists,
    getGitRootDirectory: ()=>cachedGitDirectory,
    getGradleRootDirectory: ()=>cachedGradleRootDirectory
}

function cachedSafeReadFile(filename) {
    if(fs.existsSync(filename)) return cachedFs.readFileSync(filename);
    else return Buffer.from([]);
}

function safeReadFile(filename) {
    if(fs.existsSync(filename)) return fs.readFileSync(filename);
    else return Buffer.from([]);
}

function safeWriteFileEventually(fileName, content) {
    var dir = path.dirname(fileName);

    if(fs.existsSync(dir)) {
        dirMadeWrite();
    } else {
        fs.mkdir(dir, {recursive: true}, function(err) {
            if (err) reportNodeJSFileError(err, fileName, (new Error()).stack);
            else dirMadeWrite();
        })
    }

    function dirMadeWrite() {
        fs.writeFile(fileName, content, function(err) {
            if (err) reportNodeJSFileError(err, fileName, (new Error()).stack);
        })
    }
}

function reportNodeJSFileError(err, file, stack) {
    if(stack) err.stack = stack;
    androidStudioLogging.sendTreeLocationMessage(err, file, "ERROR");
}

function safeWriteFile(fileName, content) {
    var dir = path.dirname(fileName);
    createDirectoryIfNotExist(dir);

    fs.writeFileSync(fileName, content);
}

function addToGitignore(globToAdd) {
    var gitRoot = cachedGitDirectory;
    if(!gitRoot) return false;

    var gitignore = path.join(gitRoot, ".gitignore");
    //if it doesn't exist, just create it with the required content
    if(!fs.existsSync(gitignore)) {
        fs.writeFileSync(gitignore, globToAdd + "\n");
        return true;
    }
    
    var gContent = fs.readFileSync(gitignore).toString();
    var gLines = gContent.split(/\r?\n/);

    //early exit if the gitignore already has the path
    if (gLines.includes(globToAdd)) return true;
    else gLines.push(globToAdd);

    fs.writeFileSync(gitignore, gLines.join("\n"));
    return true;
}

function createDirectoryIfNotExist(fileName) {
    var dirName = fileName;
    
    if(!fs.existsSync(dirName)) {
        fs.mkdirSync(dirName, {recursive: true});
    }
}

function deleteIfExists(file) {
    if (fs.existsSync(dir)) {
        if (fs.statSync(dir).isDirectory()) cleanDirectory(file, [], false);
        else fs.unlinkSync(file);
    }
}

function cleanDirectory(dir, dontDeleteFiles, dontDeleteTopDirectory) {
    if (!fs.existsSync(dir)) return;
    else if(fs.statSync(dir).isDirectory() == false) return;
    
    if(dontDeleteFiles === undefined) dontDeleteFiles = [];
    
    var files = fs.readdirSync(dir, { withFileTypes: true });
    var filesLeft = files.length;
    files.forEach(x=> {
        var name = path.join(dir, x.name);
        if(x.isFile()) {
            if(!dontDeleteFiles.includes(name)) {
                fs.unlinkSync(name);
                filesLeft--;
            }
        } else if(x.isDirectory()) {
            if(cleanDirectory(name, dontDeleteFiles, false)) filesLeft--;
        }
    });

    try {
        if(filesLeft == 0 && !dontDeleteTopDirectory) fs.rmdirSync(dir);
    } catch(e) {
        return false;
    }

    return filesLeft == 0;
}

function getGitRootDirectory() {
    var dir = process.cwd().split(path.sep);
    while(true) {
        var dirPath = dir.join(path.sep);
        if(fs.existsSync(path.join(dirPath, ".git"))) break;
        else dir.pop();

        if(dir.length == 0) return undefined;
    }
    return dir.join(path.sep);
}

function getGradleRootDirectory() {
    var dir = process.cwd().split(path.sep);
    while (true) {
        var dirPath = dir.join(path.sep);
        if (fs.existsSync(path.join(dirPath, "build.gradle"))) break;
        else dir.pop();

        if (dir.length == 0) return undefined;
    }
    return dir.join(path.sep);
}