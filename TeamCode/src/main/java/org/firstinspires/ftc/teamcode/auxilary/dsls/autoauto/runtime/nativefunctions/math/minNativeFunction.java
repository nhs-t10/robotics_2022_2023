package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.nativefunctions.math;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoNumericValue;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoPrimitive;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.errors.ManagerSetupException;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.NativeFunction;

public class minNativeFunction extends NativeFunction {
    @Override
    public String[] getArgNames() {
        return new String[] { "values..." };
    }

    @Override
    public AutoautoPrimitive call(AutoautoPrimitive thisArg, AutoautoPrimitive[] args) throws ManagerSetupException {
        float min = Float.MAX_VALUE;
        for(AutoautoPrimitive p : args) {
            if(p instanceof AutoautoNumericValue) {
                float v = ((AutoautoNumericValue)p).getFloat();
                if(v < min) min = v;
            }
        }
        return new AutoautoNumericValue(min);
    }
}
