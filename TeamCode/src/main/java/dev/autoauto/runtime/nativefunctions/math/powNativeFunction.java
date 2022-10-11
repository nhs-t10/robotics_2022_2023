package dev.autoauto.runtime.nativefunctions.math;

import dev.autoauto.runtime.values.AutoautoNumericValue;
import dev.autoauto.runtime.values.AutoautoValue;
import dev.autoauto.runtime.values.AutoautoUndefined;
import dev.autoauto.runtime.NativeFunction;

public class powNativeFunction extends NativeFunction {
    @Override
    public String[] getArgNames() {
        return new String[] { "base", "power" };
    }

    @Override
    public AutoautoValue call(AutoautoValue thisArg, AutoautoValue[] args) {
        if(args.length < 2) return new AutoautoUndefined();

        if(args[0] instanceof AutoautoNumericValue && args[1] instanceof AutoautoNumericValue) {
            return new AutoautoNumericValue(Math.pow(
                    ((AutoautoNumericValue)args[0]).getDouble(),
                    ((AutoautoNumericValue)args[1]).getDouble()
                    ));
        }
        return new AutoautoUndefined();
    }
}
