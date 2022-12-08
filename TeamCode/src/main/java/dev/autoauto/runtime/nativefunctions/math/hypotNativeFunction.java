package dev.autoauto.runtime.nativefunctions.math;

import dev.autoauto.runtime.values.AutoautoNumericValue;
import dev.autoauto.runtime.values.AutoautoValue;
import dev.autoauto.runtime.NativeFunction;

public class hypotNativeFunction extends NativeFunction {
    @Override
    public String[] getArgNames() {
        return new String[] { "values..." };
    }

    @Override
    public AutoautoValue call(AutoautoValue thisArg, AutoautoValue[] args) {
        float sum = 0;
        for(AutoautoValue p : args) {
            if(p instanceof AutoautoNumericValue) {
                sum += Math.pow(((AutoautoNumericValue)p).getDouble(), 2);
            }
        }
        return new AutoautoNumericValue(Math.sqrt(sum));
    }
}
