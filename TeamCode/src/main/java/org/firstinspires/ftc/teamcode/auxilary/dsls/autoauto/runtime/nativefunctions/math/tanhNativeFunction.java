package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.nativefunctions.math;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoNumericValue;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoPrimitive;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoUndefined;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.errors.ManagerSetupException;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.NativeFunction;

public class tanhNativeFunction extends NativeFunction {
    @Override
    public String[] getArgNames() {
        return new String[] { "value" };
    }

    @Override
    public AutoautoPrimitive call(AutoautoPrimitive thisArg, AutoautoPrimitive[] args) throws ManagerSetupException {
        if(args.length > 0 && args[0] instanceof AutoautoNumericValue) {
            float v = ((AutoautoNumericValue)args[0]).getFloat();
            double a = Math.exp(+v), b = Math.exp(-v);;

            return new AutoautoNumericValue(a == Double.POSITIVE_INFINITY ? 1 : b == Double.POSITIVE_INFINITY ? -1 : (a - b) / (a + b));
        }
        return new AutoautoUndefined();
    }
}
