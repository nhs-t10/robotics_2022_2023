package dev.autoauto.runtime.values.prototype.number;

import dev.autoauto.runtime.values.AutoautoNumericValue;
import dev.autoauto.runtime.values.AutoautoValue;
import dev.autoauto.runtime.NativeFunction;

public class ClipFunction extends NativeFunction {
    @Override
    public String[] getArgNames() {
        return new String[] {"Minimum", "Maximum"};
    }

    @Override
    public AutoautoValue call(AutoautoValue thisArg, AutoautoValue[] args) {
        AutoautoNumericValue thisNumber = (AutoautoNumericValue) thisArg;

        //if the user didn't give us a number, just return the same old value
        if(args.length == 0) return thisNumber;

        double minN = args[0].castToNumber().value;

        double t = thisNumber.value;
        double mN = Math.max(minN, t);

        if(args.length == 1) return new AutoautoNumericValue(mN);

        double maxN = args[1].castToNumber().value;

        return new AutoautoNumericValue(Math.min(mN, maxN));
    }
}

