package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.nativefunctions.math;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoNumericValue;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoPrimitive;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoUndefined;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.NativeFunction;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.errors.ManagerSetupException;

public class clipNativeFunction extends NativeFunction {
    @Override
    public String[] getArgNames() {
        return new String[] { "value", "low", "high" };
    }

    @Override
    public AutoautoPrimitive call(AutoautoPrimitive thisArg, AutoautoPrimitive[] args) throws ManagerSetupException {
        if(args.length > 3 && args[0] instanceof AutoautoNumericValue
            && args[1] instanceof AutoautoNumericValue
            && args[2] instanceof AutoautoNumericValue) {
            double v = ((AutoautoNumericValue)args[0]).getDouble();
            double low = ((AutoautoNumericValue)args[1]).getDouble();
            double high = ((AutoautoNumericValue)args[2]).getDouble();

            return new AutoautoNumericValue(Math.max(Math.min(v, high), low));
        }
        if(args.length > 0) return args[0];
        else return new AutoautoUndefined();
    }
}
