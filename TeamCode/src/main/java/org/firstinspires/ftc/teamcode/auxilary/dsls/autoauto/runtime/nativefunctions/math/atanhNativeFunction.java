package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.nativefunctions.math;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoNumericValue;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoPrimitive;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoUndefined;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.errors.ManagerSetupException;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.NativeFunction;

public class atanhNativeFunction extends NativeFunction {
    @Override
    public String[] getArgNames() {
        return new String[] { "value" };
    }

    @Override
    public AutoautoPrimitive call(AutoautoPrimitive thisArg, AutoautoPrimitive[] args) throws ManagerSetupException {
        if(args.length < 1) return new AutoautoUndefined();

        if(args[0] instanceof AutoautoNumericValue) {
            float x = ((AutoautoNumericValue)args[0]).getFloat();
            if(x >= 1) return new AutoautoUndefined();

            return new AutoautoNumericValue(Math.log((1+x)/(1-x)) / 2);
        }
        return new AutoautoUndefined();
    }
}
