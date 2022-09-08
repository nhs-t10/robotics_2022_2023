package dev.autoauto.runtime.nativefunctions.math;

import dev.autoauto.runtime.values.AutoautoNumericValue;
import dev.autoauto.runtime.values.AutoautoValue;
import dev.autoauto.runtime.NativeFunction;

public class maxNativeFunction extends NativeFunction {
    @Override
    public String[] getArgNames() {
        return new String[] { "values..." };
    }

    @Override
    public AutoautoValue call(AutoautoValue thisArg, AutoautoValue[] args) {
        double max = Double.MAX_VALUE;
        for(AutoautoValue p : args) {
            if(p instanceof AutoautoNumericValue) {
                double v = ((AutoautoNumericValue)p).getDouble();
                if(v > max) max = v;
            }
        }
        return new AutoautoNumericValue(max);
    }
}
