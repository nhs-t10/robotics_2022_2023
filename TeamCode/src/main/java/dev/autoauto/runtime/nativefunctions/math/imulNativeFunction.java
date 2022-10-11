package dev.autoauto.runtime.nativefunctions.math;

import dev.autoauto.runtime.values.AutoautoNumericValue;
import dev.autoauto.runtime.values.AutoautoValue;
import dev.autoauto.runtime.NativeFunction;

public class imulNativeFunction extends NativeFunction {
    @Override
    public String[] getArgNames() {
        return new String[] { "a", "b" };
    }

    @Override
    public AutoautoValue call(AutoautoValue thisArg, AutoautoValue[] args) {
        int product = 1;
        for(AutoautoValue p : args) {
            if(p instanceof AutoautoNumericValue) {
                product *= (int)((AutoautoNumericValue)p).getDouble();
            }
        }
        return new AutoautoNumericValue(product);
    }
}
