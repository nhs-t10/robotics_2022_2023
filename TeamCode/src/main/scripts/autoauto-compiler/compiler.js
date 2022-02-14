var fs = require("fs");
var path = require("path");

var aaParser = require("./text-to-syntax-tree");
var parserTools = require("../script-helpers/parser-tools");

var crc = require("../script-helpers/crc-string");

var GITIGNORED = ["*__autoauto.java"];

var DEFAULT_SERVOS = [];
var DEFAULT_CRSERVOS = [];

var directory = __dirname.split(path.sep);

var runChecks = require("./checks");
var makeTestFile = require("./make-test");
const syntaxTreeToBytecode = require("./syntax-tree-to-bytecode");

var templates = {
    "template": fs.readFileSync(path.join(__dirname, "data" + path.sep + "template.notjava")).toString()
}

var rootDirectory = directory.slice(0, directory.indexOf("TeamCode")).join(path.sep);


//update gitignore with autoauto files
if(!fs.existsSync(path.join(rootDirectory, ".gitignore"))) {
    fs.writeFileSync(path.join(rootDirectory, ".gitignore"), "");
}
var gitignore = fs.readFileSync(path.join(rootDirectory, ".gitignore")).toString();
var gitignoreLines = gitignore.split(/\r?\n/);

for(var i = 0; i < GITIGNORED.length; i++) {
    if(gitignoreLines.indexOf(GITIGNORED[i]) == -1) gitignoreLines.push(GITIGNORED[i]);
}

gitignore = gitignoreLines.join("\n");
fs.writeFileSync(path.join(rootDirectory, ".gitignore"), gitignore); //SAFE

var srcDirectory = directory.slice(0, directory.indexOf("src") + 1).join(path.sep);

var compiledResultDirectory = path.join(srcDirectory, "../gen/org/firstinspires/ftc/teamcode/__compiledautoauto");
createDirectoryIfNotExist(compiledResultDirectory);

var TESTS_PACKAGE = "org.firstinspires.ftc.teamcode.unitTests.__testedautoauto";
var testsDirectory = path.join(srcDirectory, "test/java/" + TESTS_PACKAGE.replace(/\./g, path.sep));

var autoautoFiles = loadAutoautoFilesFromFolder(srcDirectory);
var alreadyUsedAutoautoFileNames = {};

var writtenFiles = [];
var requiredTests = [];

for(var i = 0; i < autoautoFiles.length; i++) {
    var fileSource = fs.readFileSync(autoautoFiles[i]).toString();
    var folder = path.dirname(autoautoFiles[i]);
    var shortButUniqueFolder = folder.replace(srcDirectory, "").toLowerCase();
    shortButUniqueFolder = shortButUniqueFolder.substring(shortButUniqueFolder.indexOf("teamcode"));
    var packageFolder = shortButUniqueFolder

    var package = "org.firstinspires.ftc.teamcode.__compiledautoauto." + packageFolder.replace(/\/|\\/g, ".");

    var fileName = autoautoFiles[i].substring(autoautoFiles[i].lastIndexOf(path.sep) + 1);
    if (fileName.includes(".macro")) throw "Macro is a bad idea you idiots!";
    var templateUsed = "template";
    var className = jClassIfy(fileName)
        .replace(".autoauto", "__autoauto");

    var classNameNoConflict = className;
    if(alreadyUsedAutoautoFileNames[className]) classNameNoConflict += "__" + alreadyUsedAutoautoFileNames[className];

    if(!alreadyUsedAutoautoFileNames[className]) alreadyUsedAutoautoFileNames[className] = 0;
    alreadyUsedAutoautoFileNames[className]++;

    var javaFileName = className + ".java";

    var resultFile = path.join(compiledResultDirectory, packageFolder, javaFileName);
    createDirectoryIfNotExist(resultFile);

    var uncommentedFileSource = parserTools.stripComments(fileSource);

    var parsedModel, parsedBytecode;
    try {
        parsedModel = aaParser.parse(fileSource);
    } catch(e) {
        parsedModel = e;
    }
    
    parsedBytecode = syntaxTreeToBytecode(parsedModel);

    if(!process.argv.includes("--no-checks")) {
        var checksPassed = runChecks(parsedModel, folder, fileName, fileSource, uncommentedFileSource);
        if(!checksPassed) continue;
    }
    if(parsedModel instanceof Error) continue;

    var frontMatter = transformFrontmatterTreeIntoJSON(parsedModel.frontMatter);

    var javaCreationCode = astJavaify(parsedModel, frontMatter);

    var jsonSettingCode = getDebugJsonSettingCode(parsedModel);

    fs.writeFileSync(resultFile, //SAFE
        processTemplate(templates[templateUsed], className, frontMatter, javaCreationCode, autoautoFiles[i], jsonSettingCode, package, classNameNoConflict)
    );

    requiredTests.push({
        className: className, package: package, frontMatter: frontMatter
    });
    writtenFiles.push(resultFile);

}

//clean leftover java files from deleted autoauto modes.
clearDirectory(compiledResultDirectory, writtenFiles);

makeTestFile(requiredTests, testsDirectory, TESTS_PACKAGE);


function astJavaify(parsedModel, frontMatter) {
    var cMode;
    if(fs.existsSync(__dirname + "/compiler-modes/" + frontMatter.compilerMode + "/index.js")) {
        cMode = require("./compiler-modes/" + frontMatter.compilerMode);
    } else {
        cMode = require("./compiler-modes/default");
    }

    return cMode(parsedModel);
}

function getDebugJsonSettingCode(parsedModel) {
    var programOutline = Object.fromEntries(parsedModel.statepaths.map(x=>[x.label.value, x.statepath.states.length]));
    var programOutlineJson = JSON.stringify(programOutline);

    //double-stringify it to make the JSON into valid Java
    return `String simpleProgramJson = ${JSON.stringify(programOutlineJson)};`
}

function jStringEnc(str) {
    return JSON.stringify("" + str);
}

function jClassIfy(str) {
    return str.split("-").map(x=>capitalize(x)).join("");
}
function capitalize(str) {
    return str[0].toUpperCase() + str.substring(1);
}

function clearDirectory(dir, dontDeleteFiles) {
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
            if(clearDirectory(name, dontDeleteFiles)) filesLeft--;
        }
    });

    if(filesLeft == 0) fs.rmdirSync(dir);
    return filesLeft == 0;
}

function createDirectoryIfNotExist(fileName) {
    var dirName = path.dirname(fileName);
    if(!fs.existsSync(dirName)) {
        fs.mkdirSync(dirName, {recursive: true});
    }
}

function processTemplate(template, className, frontMatter, javaCreationCode, sourceFileName, jsonSettingCode, package, classNameNoConflict) {
    return template
        .replace("public class template", "public class " + className)
        .replace("/*NSERVO_NAMES*/", buildServoNames(frontMatter.servos))
        .replace("/*NSERVOS*/", buildServos(frontMatter.servos))
        .replace("/*JAVA_CREATION_CODE*/", javaCreationCode)
        .replace("/*CRSERVO_NAMES*/", buildCrServoNames(frontMatter.crServos))
        .replace("/*CRSERVOS*/", buildCrServos(frontMatter.crServos))
        .replace("/*PACKAGE_DECLARATION*/", "package " + package + ";")
        .replace("/*JSON_SETTING_CODE*/", jsonSettingCode)
        .replace("/*NO_CONFLICT_NAME*/", classNameNoConflict)
        .replace("/*SOURCE_FILE_NAME*/", JSON.stringify(sourceFileName).slice(1, -1))
        .replace("/*ERROR_STACK_TRACE_HEIGHT*/", (+frontMatter.errorStackTraceHeight) || 1)
        .replace("/*COMPAT_MODE_SETTING*/", getCompatModeSetter(frontMatter));
}

function getCompatModeSetter(frontMatter) {
    var keys = Object.keys(frontMatter);

    var flagRegex = /^[a-z]*flag_/;
    var flagPrefix = "\t@";

    var flagKeys = keys.filter(x=>flagRegex.test(x));

    var setters = flagKeys.map(x=>`runtime.rootModule.globalScope.systemSet(${jStringEnc(flagPrefix + x)}, new AutoautoBooleanValue(true));`);

    return setters.join("\n");
}

function buildServoNames(servos) {
    if(servos === undefined) servos = DEFAULT_SERVOS;
    return servos.map(x=> `"${x}"`).join(", ");
}

function buildCrServoNames(crServos) {
    if(crServos === undefined) crServos = DEFAULT_CRSERVOS;
    return crServos.map(x=> `"${x}"`).join(", ");
}

function buildCrServos(crServos) {
    if(crServos === undefined) crServos = DEFAULT_CRSERVOS;
    return crServos.map(x=> `hardwareMap.get(CRServo.class, "${x}")`).join(", ");
}

function buildServos(servos) {
    if(servos === undefined) servos = DEFAULT_SERVOS;
    return servos.map(x=> `hardwareMap.get(Servo.class, "${x}")`).join(", ");
}

function transformFrontmatterTreeIntoJSON(srcFmTree) {
    if(srcFmTree == null) return {};

    var fm = {};

    srcFmTree.values.forEach(x=>{
        //3 possibilities: string, boolean, & number. 
        fm[x.key.value] = x.value.str || x.value.value || x.value.v;
    });

    return fm;
}

function loadAutoautoFilesFromFolder(folder) {
    let results = [];

    let folderContents = fs.readdirSync(folder, {
        withFileTypes: true
    });

    for(var i = 0; i < folderContents.length; i++) {
        let subfile = folderContents[i];

        if(subfile.isDirectory()) {
            results = results.concat(loadAutoautoFilesFromFolder(path.resolve(folder, subfile.name)));
        } else if(subfile.isFile() && subfile.name.endsWith(".autoauto")) {
            results.push(path.resolve(folder, subfile.name));
        }
    }

    return results;
}