package dev.autoauto.runtime.nativefunctions.math;

import dev.autoauto.runtime.values.AutoautoNumericValue;
import dev.autoauto.runtime.values.AutoautoValue;
import dev.autoauto.runtime.values.AutoautoUndefined;
import dev.autoauto.runtime.NativeFunction;

public class asinhNativeFunction extends NativeFunction {
    @Override
    public String[] getArgNames() {
        return new String[] { "value" };
    }

    @Override
    public AutoautoValue call(AutoautoValue thisArg, AutoautoValue[] args) {
        if(args.length < 1) return new AutoautoUndefined();
        if(args[0] instanceof AutoautoNumericValue) {
            return new AutoautoNumericValue(asinh(((AutoautoNumericValue)args[0]).getDouble()));
        }
        return new AutoautoUndefined();
    }

    private double asinh(double x) {
        double absX = Math.abs(x), w;
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
            double t = x * x;
            w = (float) Math.log1p(absX + t / (1 + Math.sqrt(1 + t)));
        }
        return x > 0 ? w : -w;
    }
}
