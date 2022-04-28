package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.nativefunctions.math;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoNumericValue;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoPrimitive;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.errors.ManagerSetupException;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.NativeFunction;

public class imulNativeFunction extends NativeFunction {
    @Override
    public String[] getArgNames() {
        return new String[] { "a", "b" };
    }

    @Override
    public AutoautoPrimitive call(AutoautoPrimitive thisArg, AutoautoPrimitive[] args) throws ManagerSetupException {
        int product = 1;
        for(AutoautoPrimitive p : args) {
            if(p instanceof AutoautoNumericValue) {
                product *= (int)((AutoautoNumericValue)p).getFloat();
            }
        }
        return new AutoautoNumericValue(product);
    }
}
