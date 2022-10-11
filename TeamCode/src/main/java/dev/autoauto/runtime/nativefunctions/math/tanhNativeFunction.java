package dev.autoauto.runtime.nativefunctions.math;

import dev.autoauto.runtime.values.AutoautoNumericValue;
import dev.autoauto.runtime.values.AutoautoValue;
import dev.autoauto.runtime.values.AutoautoUndefined;
import dev.autoauto.runtime.NativeFunction;

public class tanhNativeFunction extends NativeFunction {
    @Override
    public String[] getArgNames() {
        return new String[] { "value" };
    }

    @Override
    public AutoautoValue call(AutoautoValue thisArg, AutoautoValue[] args) {
        if(args.length > 0 && args[0] instanceof AutoautoNumericValue) {
            double v = ((AutoautoNumericValue)args[0]).getDouble();
            double a = Math.exp(+v), b = Math.exp(-v);;

            return new AutoautoNumericValue(a == Double.POSITIVE_INFINITY ? 1 : b == Double.POSITIVE_INFINITY ? -1 : (a - b) / (a + b));
        }
        return new AutoautoUndefined();
    }
}
