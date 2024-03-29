package dev.autoauto.runtime.nativefunctions.math;

import dev.autoauto.runtime.values.AutoautoNumericValue;
import dev.autoauto.runtime.values.AutoautoValue;
import dev.autoauto.runtime.NativeFunction;

public class minNativeFunction extends NativeFunction {
    @Override
    public String[] getArgNames() {
        return new String[] { "values..." };
    }

    @Override
    public AutoautoValue call(AutoautoValue thisArg, AutoautoValue[] args) {
        double min = Double.MAX_VALUE;
        for(AutoautoValue p : args) {
            if(p instanceof AutoautoNumericValue) {
                double v = ((AutoautoNumericValue)p).getDouble();
                if(v < min) min = v;
            }
        }
        return new AutoautoNumericValue(min);
    }
}
