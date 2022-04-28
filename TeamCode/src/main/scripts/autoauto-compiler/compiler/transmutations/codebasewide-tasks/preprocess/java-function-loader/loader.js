var path = require("path");
var fs = require("fs");
var crypto = require("crypto");

var directory = __dirname.split(path.sep);
var rootDirectory = directory.slice(0, directory.indexOf("TeamCode")).join(path.sep);
var managersDir = path.join(rootDirectory, "TeamCode/src/main/java/org/firstinspires/ftc/teamcode/managers");

var cache = require("../../../../../../cache");

var functionLoaderConfig = require("../../../../../config");

var cacheManagers = cache.get(functionLoaderConfig.CACHE_KEY, { managers: {} });

if (cacheManagers.cacheVersion != functionLoaderConfig.CACHE_VERSION) cacheManagers = { managers: {} };

var robotFunctionsDirectory = path.join(rootDirectory, "TeamCode/gen/org/firstinspires/ftc/teamcode/auxilary/dsls/autoauto/runtime/robotfunctions");
if (!fs.existsSync(robotFunctionsDirectory)) cacheManagers = { data: {} };

cacheManagers.cacheVersion = functionLoaderConfig.CACHE_VERSION;

var managerArgs = {};

var generateAaMethods = require("./parse-and-generate-aa-methods.js");
var deleteUnusedMethodClasses = require("./delete-unused-method-classes.js");
const safeFsUtils = require("../../../../../../script-helpers/safe-fs-utils");

if (!fs.existsSync(managersDir)) throw "Managers directory `" + managersDir + "` doesn't exist";

var managers = loadManagersFromFolder(managersDir);
module.exports = async function() {
    var methods = [];
    for (var i = 0; i < managers.length; i++) {
        var manager = managers[i];
        var fileContent = fs.readFileSync(manager).toString();

        var sha = crypto.createHash("sha256").update(fileContent).digest("hex");

        if (cacheManagers.managers[manager] === undefined) cacheManagers.managers[manager] = {};

        if (cacheManagers.managers[manager].javaSha === sha) {
            methods = methods.concat(cacheManagers.managers[manager].data.methods);
        } else {
            cacheManagers.managers[manager].javaSha = sha;
            var preexistingNames = methods.map(x => x.shimClassFunction.nameToUseInAutoauto).flat();

            var generated = generateAaMethods(fileContent, preexistingNames);

            if (generated) cacheManagers.managers[manager].data = generated;

            methods = methods.concat(cacheManagers.managers[manager].data.methods);
        }
    }

    deleteUnusedMethodClasses(methods.map(x => x.shimClassFunction.javaImplementationClass).flat());

    var robotFunctionLoaderAddress = path.join(rootDirectory, "TeamCode/gen/org/firstinspires/ftc/teamcode/auxilary/dsls/autoauto/runtime/RobotFunctionLoader.java");
    if (!fs.existsSync(path.dirname(robotFunctionLoaderAddress))) fs.mkdirSync(path.dirname(robotFunctionLoaderAddress), { recursive: true });

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
    safeFsUtils.safeWriteFileEventually(robotFunctionLoaderAddress, robotFunctionLoader);

    cache.save("autoauto-managers", cacheManagers);
    return cacheManagers.managers;
};

function makeManagerName(name) {
    if (name == "") return "";

    if (managerArgs[name]) return managerArgs[name];

    var basename = name.split(".").slice(-1)[0].replace(/Manager$/, "");

    managerArgs[name] = "man" + basename;
    return managerArgs[name] || "";
}

function loadManagersFromFolder(folder) {
    let results = [];

    let folderContents = fs.readdirSync(folder, {
        withFileTypes: true
    });

    for (var i = 0; i < folderContents.length; i++) {
        let subfile = folderContents[i];

        if (subfile.isDirectory()) {
            results = results.concat(loadManagersFromFolder(path.join(folder, subfile.name)));
        } else if (subfile.isFile() && (subfile.name.endsWith("Manager.java") || subfile.name == "PaulMath.java")) {
            results.push(path.join(folder, subfile.name));
        }
    }

    return results;
}