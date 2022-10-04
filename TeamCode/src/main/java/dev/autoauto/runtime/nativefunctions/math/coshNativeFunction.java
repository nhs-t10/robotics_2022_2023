package dev.autoauto.runtime.nativefunctions.math;

import dev.autoauto.runtime.values.AutoautoNumericValue;
import dev.autoauto.runtime.values.AutoautoValue;
import dev.autoauto.runtime.values.AutoautoUndefined;
import dev.autoauto.runtime.NativeFunction;

public class coshNativeFunction extends NativeFunction {
    @Override
    public String[] getArgNames() {
        return new String[] { "value" };
    }

    @Override
    public AutoautoValue call(AutoautoValue thisArg, AutoautoValue[] args) {
        if(args.length > 0 && args[0] instanceof AutoautoNumericValue) {
            double v = ((AutoautoNumericValue)args[0]).getDouble();
            double e = Math.exp(v);

            return new AutoautoNumericValue((e + 1 / e) / 2.0);
        }
        return new AutoautoUndefined();
    }
}
