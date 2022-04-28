package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.nativefunctions.math;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoNumericValue;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoPrimitive;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoUndefined;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.errors.ManagerSetupException;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.NativeFunction;

public class asinhNativeFunction extends NativeFunction {
    @Override
    public String[] getArgNames() {
        return new String[] { "value" };
    }

    @Override
    public AutoautoPrimitive call(AutoautoPrimitive thisArg, AutoautoPrimitive[] args) throws ManagerSetupException {
        if(args.length < 1) return new AutoautoUndefined();
        if(args[0] instanceof AutoautoNumericValue) {
            float x = ((AutoautoNumericValue)args[0]).getFloat();

            return new AutoautoNumericValue(asinh(x));
        }
        return new AutoautoUndefined();
    }

    private float asinh(float x) {
        float absX = Math.abs(x), w;
        if (absX < 3.725290298461914e-9) // |x| < 2^-28
        {
            return x;
        }
        if (absX > 268435456) // |x| > 2^28
        {
            w = (float) (Math.log(absX) + 0.6931471805599453); //ln(2)
        } else if (absX > 2) // 2^28 >= |x| > 2
        {
            w = (float) Math.log(2 * absX + 1 / (Math.sqrt(x * x + 1) + absX));
        } else {
            float t = x * x;
            w = (float) Math.log1p(absX + t / (1 + Math.sqrt(1 + t)));
        }
        return x > 0 ? w : -w;
    }
}
