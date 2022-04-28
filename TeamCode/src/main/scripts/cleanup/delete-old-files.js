var path = require("path");
var deleteIfExists = require("../script-helpers/delete-if-exists");

var oldFiles = [
    path.join(__dirname, "../../java/org/firstinspires/ftc/teamcode/auxilary/buildhistory"),
    path.join(__dirname, "../../java/org/firstinspires/ftc/teamcode/auxilary/dsls/autoauto/runtime/RobotFunctionLoader.java"),
    path.join(__dirname, "../../java/org/firstinspires/ftc/teamcode/auxilary/dsls/autoauto/runtime/robotfunctions"),
    path.join(__dirname, "../../java/org/firstinspires/ftc/teamcode/__compiledcontrols"),
    path.join(__dirname, "../../java/org/firstinspires/ftc/teamcode/__compiledautoauto")
];
oldFiles.forEach(x=>{
    console.warn("Deleting " + x);
    deleteIfExists(x)
});