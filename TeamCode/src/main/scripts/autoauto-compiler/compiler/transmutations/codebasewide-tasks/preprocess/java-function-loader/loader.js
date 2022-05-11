var path = require("path");
var fs = require("fs");
var crypto = require("crypto");

var directory = __dirname.split(path.sep);
var rootDirectory = directory.slice(0, directory.indexOf("TeamCode")).join(path.sep);
var managersDir = path.join(rootDirectory, "TeamCode/src/main/java/org/firstinspires/ftc/teamcode/managers");

var cache = require("../../../../../../cache");

var functionLoaderConfig = require("../../../../../config");

var managerArgs = {};

var generateAaMethods = require("./parse-and-generate-aa-methods.js");
const safeFsUtils = require("../../../../../../script-helpers/safe-fs-utils");
const folderScanner = require("../../../../folder-scanner");
const commandLineInterface = require("../../../../../../command-line-interface");

if (!fs.existsSync(managersDir)) throw "Managers directory `" + managersDir + "` doesn't exist";

module.exports = async function(writtenFiles) {
    var cacheManagers = getCacheManagers();
    var managers = await loadManagersFromFolder(managersDir);
    
    var methods = [];
    for (var i = 0; i < managers.length; i++) {
        var manager = managers[i];
        var fileContent = fs.readFileSync(manager).toString();

        var sha = crypto.createHash("sha256").update(fileContent).digest("hex");

        if (cacheManagers.managers[manager] === undefined) cacheManagers.managers[manager] = { methods: [] };

        if (cacheManagers.managers[manager].javaSha === sha) {
            methods = methods.concat(cacheManagers.managers[manager].methods);
        } else {
            var preexistingNames = methods.map(x => x.shimClassFunction.nameToUseInAutoauto).flat();

            var generated = generateAaMethods(fileContent, preexistingNames);

            cacheManagers.managers[manager] = {
                methods: generated || [],
                javaSha: sha
            };
            methods = methods.concat(cacheManagers.managers[manager].methods);
        }
    }

    var robotFunctionLoaderAddress = path.join(rootDirectory, "TeamCode/gen/org/firstinspires/ftc/teamcode/auxilary/dsls/autoauto/runtime/RobotFunctionLoader.java");

    var robotFunctionsTemplate = require("./make-robotfunctionloader.js");
    var robotFunctionLoader = robotFunctionsTemplate(
        methods
            .map(x => ({
                funcname: x.shimClassFunction.nameToUseInAutoauto,
                varname: "func_" + x.shimClassFunction.nameToUseInAutoauto,
                classname: x.shimClassFunction.javaImplementationClass,
                manager: makeManagerName(x.originalSourceClass)
            })
            ),
        Object.entries(managerArgs),
    );
    writtenFiles[robotFunctionLoaderAddress] = robotFunctionLoader;
    
    addAllRobotfunctionFilesToWrittenFiles(writtenFiles, methods);

    cache.save("autoauto-managers", cacheManagers);
    return cacheManagers.managers;
};

function getCacheManagers() {
    const DEFAULT_CACHE_MANAGERS = { managers: { }, cacheVersion: functionLoaderConfig.CACHE_KEY };
    
    
    if (commandLineInterface["no-cache"]) return DEFAULT_CACHE_MANAGERS;
    
    var cacheManagers = cache.get(functionLoaderConfig.CACHE_KEY, DEFAULT_CACHE_MANAGERS);

    if (cacheManagers.cacheVersion != functionLoaderConfig.CACHE_VERSION) cacheManagers = DEFAULT_CACHE_MANAGERS;
    cacheManagers.cacheVersion = functionLoaderConfig.CACHE_VERSION;
    
    return cacheManagers;

}

function addAllRobotfunctionFilesToWrittenFiles(writtenFiles, methods) {
    for(const method of methods) {
        writtenFiles[method.shimClassFunction.javaImplementationFile] = true;
    }
}

function makeManagerName(name) {
    if (name == "") return "";

    if (managerArgs[name]) return managerArgs[name];

    var basename = name.split(".").slice(-1)[0].replace(/Manager$/, "");

    managerArgs[name] = "man" + basename;
    return managerArgs[name] || "";
}

async function loadManagersFromFolder(folder) {
    let results = [];
    
    const sc = folderScanner(folder, x=>(x.endsWith("Manager.java") || x == "PaulMath.java"));
    
    while(true) {
        const file = await sc.next();
        if(file.done) break;
        else results.push(file.value);
    }

    return results;
}