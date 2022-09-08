package dev.autoauto.runtime.nativefunctions.math;

import dev.autoauto.runtime.values.AutoautoNumericValue;
import dev.autoauto.runtime.values.AutoautoValue;
import dev.autoauto.runtime.values.AutoautoUndefined;
import dev.autoauto.runtime.NativeFunction;

public class asinNativeFunction extends NativeFunction {
    @Override
    public String[] getArgNames() {
        return new String[] { "value" };
    }

    @Override
    public AutoautoValue call(AutoautoValue thisArg, AutoautoValue[] args) {
        if(args.length < 1) return new AutoautoUndefined();
        if(args[0] instanceof AutoautoNumericValue) {
            return new AutoautoNumericValue(Math.asin(((AutoautoNumericValue)args[0]).getDouble()));
        }
        return new AutoautoUndefined();
    }
}
