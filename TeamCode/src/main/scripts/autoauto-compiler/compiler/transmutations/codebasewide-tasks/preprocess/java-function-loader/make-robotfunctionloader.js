"use strict";

module.exports = function(loadFunctionsSrc, margs) {
    return `package dev.autoauto.runtime;

    import dev.autoauto.runtime.robotfunctions.*;
    import java.util.ArrayList;
    
    public class RobotFunctionLoader {

    private dev.autoauto.runtime.NativeRobotFunction ${loadFunctionsSrc.map(x=>x.varname).join(",")};

        public void loadFunctions(AutoautoRuntimeVariableScope scope) {
            ${loadFunctionsSrc.map(x=>"scope.put(\"" + x.funcname + "\"," + x.varname + ");").join("\n")}
        }
        public RobotFunctionLoader(Object... managers) {
            ${margs.map(x=>x[0] + " " + x[1] + " = null;").join("\n")}
            for(Object f : managers) {
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