package org.firstinspires.ftc.teamcode.managers.telemetry;

import org.firstinspires.ftc.teamcode.auxilary.PaulMath;
import dev.autoauto.runtime.values.AutoautoValue;
import dev.autoauto.runtime.AutoautoRuntimeVariableScope;
import dev.autoauto.runtime.NativeFunction;

public class AutoautoTelemetry {
    String programOutlineJson;
    public AutoautoRuntimeVariableScope globalScope;

    private int sendProgramJson = 2;

    public void setGlobalScope(AutoautoRuntimeVariableScope globalScope) {
        this.globalScope = globalScope;
    }

    public final String getProgramJson() {
        if(programOutlineJson != null) {
            if (sendProgramJson == 0) return "{}";
            else if (sendProgramJson == 1) return "null";
            else if (sendProgramJson == 2) return programOutlineJson;
        }
        return "null";
    }

    public void setProgramJsonSendingFlag(int t) {
        this.sendProgramJson = t;
    }

    public final void setProgramOutlineJson(String json) {
        this.programOutlineJson = json;
    }

    public String getVariableValueJson() {
        if(globalScope == null) return "null";


        StringBuilder r = new StringBuilder("{ ");
        for(String name : globalScope.getKeys()) {
            AutoautoValue val = globalScope.get(name);
            //don't add native functions, bc if we do, it'll add all the built-in functions & bloat the json
            if(!(val instanceof NativeFunction)) {
                r.append(PaulMath.JSONify(name));
                r.append(":");

                if(val == null) r.append("null");
                else r.append(val.getJSONString());

                r.append(",");
            }

        }
        //remove the last comma, or the padding space if there're no objects
        String res = r.toString();
        res = res.substring(0, res.length() - 1);
        res += "}";
        return res;
    }
}
