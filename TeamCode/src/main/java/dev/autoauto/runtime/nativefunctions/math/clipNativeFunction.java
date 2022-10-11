package dev.autoauto.runtime.nativefunctions.math;

import dev.autoauto.runtime.values.AutoautoNumericValue;
import dev.autoauto.runtime.values.AutoautoValue;
import dev.autoauto.runtime.values.AutoautoUndefined;
import dev.autoauto.runtime.NativeFunction;

public class clipNativeFunction extends NativeFunction {
    @Override
    public String[] getArgNames() {
        return new String[] { "value", "low", "high" };
    }

    @Override
    public AutoautoValue call(AutoautoValue thisArg, AutoautoValue[] args) {
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
