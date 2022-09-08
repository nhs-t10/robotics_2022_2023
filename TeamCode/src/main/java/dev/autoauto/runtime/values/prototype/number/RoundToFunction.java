package dev.autoauto.runtime.values.prototype.number;

import dev.autoauto.runtime.values.AutoautoNumericValue;
import dev.autoauto.runtime.values.AutoautoValue;
import dev.autoauto.runtime.NativeFunction;

public class RoundToFunction extends NativeFunction {
    @Override
    public String[] getArgNames() {
        return new String[] {"Precision"};
    }

    @Override
    public AutoautoValue call(AutoautoValue thisArg, AutoautoValue[] args) {
        AutoautoNumericValue thisNumber = (AutoautoNumericValue) thisArg;

        //if the user didn't give us a number, just return the same old value
        if(args.length == 0 || !(args[0] instanceof AutoautoNumericValue)) return thisNumber;

        AutoautoNumericValue rounder = (AutoautoNumericValue) args[0];

        double t = thisNumber.value;
        double r = rounder.value;

        double p = Math.pow(10, -(int)r);

        return new AutoautoNumericValue((float) (Math.round(t / p) * p));
    }
}
