module.exports = function(loadFunctionsSrc, margs) {
    return `package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime;

    import org.firstinspires.ftc.teamcode.managers.feature.FeatureManager;
    import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.robotfunctions.*;
    import java.util.ArrayList;
    
    public class RobotFunctionLoader {

    private NativeRobotFunction ${loadFunctionsSrc.map(x=>x.varname).join(",")};

        public void loadFunctions(AutoautoRuntimeVariableScope scope) {
            ${loadFunctionsSrc.map(x=>"scope.put(\"" + x.funcname + "\"," + x.varname + ");").join("\n")}
        }
        public RobotFunctionLoader(FeatureManager... managers) {
            ${margs.map(x=>x[0] + " " + x[1] + " = null;").join("\n")}
            for(FeatureManager f : managers) {
                ${margs.map(x=>"if(f instanceof " + x[0] + ") " + x[1] + " = (" + x[0] + ")f;").join("\n")}
            }
${loadFunctionsSrc.map(x=>`${x.varname} = new ${x.classname}(${x.manager});`).join("\n")}
        }
    }
    `;
}

function remExcessiveWhitespace(src) {
    return src.replace(/\n\n+/g, "\n\n");
}

function indent(lvl, src) {
    return src.split("\n").map(x=>"    ".repeat(lvl)+x).join("\n");
}