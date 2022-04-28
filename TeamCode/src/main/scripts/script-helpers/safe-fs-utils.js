var fs = require("fs");
var path = require("path");
const androidStudioLogging = require("./android-studio-logging");
const cachedFs = require("./cached-fs");


var cachedGitDirectory = getGitRootDirectory();

module.exports = {
    safeWriteFile: safeWriteFile,
    createDirectoryIfNotExist: createDirectoryIfNotExist,
    cleanDirectory: cleanDirectory,
    addToGitignore: addToGitignore,
    safeReadFile: safeReadFile,
    cachedSafeReadFile: cachedSafeReadFile,
    safeWriteFileEventually: safeWriteFileEventually,
    getGitRootDirectory: ()=>cachedGitDirectory
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

    if(!fs.existsSync(dir)) {
        fs.mkdir(dir, {recursive: true}, function(err) {
            if(err) reportNodeJSFileError(err, fileName);
            else dirMadeWrite();
        })
    } else dirMadeWrite();

    function dirMadeWrite() {
        fs.writeFile(fileName, content, function(err) {
            if(err) reportNodeJSFileError(err, fileName);
        })
    }
}

function reportNodeJSFileError(err, file) {
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
    
    //if the final term has a dot, assume it's a filename
    if(path.basename(fileName).includes(".")) dirName = path.dirname(fileName);
    
    if(!fs.existsSync(dirName)) {
        fs.mkdirSync(dirName, {recursive: true});
    }
}

function cleanDirectory(dir, dontDeleteFiles, dontDeleteTopDirectory) {
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