package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.nativefunctions.math;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoNumericValue;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoPrimitive;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.errors.ManagerSetupException;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.NativeFunction;

public class hypotNativeFunction extends NativeFunction {
    @Override
    public String[] getArgNames() {
        return new String[] { "values..." };
    }

    @Override
    public AutoautoPrimitive call(AutoautoPrimitive thisArg, AutoautoPrimitive[] args) throws ManagerSetupException {
        float sum = 0;
        for(AutoautoPrimitive p : args) {
            if(p instanceof AutoautoNumericValue) {
                sum += Math.pow(((AutoautoNumericValue)p).getFloat(), 2);
            }
        }
        return new AutoautoNumericValue(Math.sqrt(sum));
    }
}
