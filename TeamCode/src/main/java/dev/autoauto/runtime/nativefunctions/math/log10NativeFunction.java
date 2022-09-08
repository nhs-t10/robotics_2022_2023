package dev.autoauto.runtime.nativefunctions.math;

import dev.autoauto.runtime.values.AutoautoNumericValue;
import dev.autoauto.runtime.values.AutoautoValue;
import dev.autoauto.runtime.values.AutoautoUndefined;
import dev.autoauto.runtime.NativeFunction;

public class log10NativeFunction extends NativeFunction {
    @Override
    public String[] getArgNames() {
        return new String[] { "value" };
    }

    @Override
    public AutoautoValue call(AutoautoValue thisArg, AutoautoValue[] args) {
        if(args.length > 0 && args[0] instanceof AutoautoNumericValue) {
            return new AutoautoNumericValue(Math.log10(((AutoautoNumericValue)args[0]).getDouble()));
        }
        return new AutoautoUndefined();
    }
}
