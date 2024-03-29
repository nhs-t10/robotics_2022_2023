package dev.autoauto.runtime.nativefunctions.math;

import dev.autoauto.runtime.values.AutoautoNumericValue;
import dev.autoauto.runtime.values.AutoautoValue;
import dev.autoauto.runtime.values.AutoautoUndefined;
import dev.autoauto.runtime.NativeFunction;

public class acoshNativeFunction extends NativeFunction {
    @Override
    public String[] getArgNames() {
        return new String[] { "value" };
    }

    @Override
    public AutoautoValue call(AutoautoValue thisArg, AutoautoValue[] args) {
        if(args.length < 1) return new AutoautoUndefined();
        if(args[0] instanceof AutoautoNumericValue) {
            double x = ((AutoautoNumericValue)args[0]).getDouble();
            if(x < 1.0) return new AutoautoUndefined();

            return new AutoautoNumericValue(Math.log(x + Math.sqrt(x * x - 1)));
        }
        return new AutoautoUndefined();
    }
}
