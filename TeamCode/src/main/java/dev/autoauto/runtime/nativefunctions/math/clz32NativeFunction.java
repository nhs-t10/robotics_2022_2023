package dev.autoauto.runtime.nativefunctions.math;

import dev.autoauto.runtime.values.AutoautoNumericValue;
import dev.autoauto.runtime.values.AutoautoValue;
import dev.autoauto.runtime.values.AutoautoUndefined;
import dev.autoauto.runtime.NativeFunction;

public class clz32NativeFunction extends NativeFunction {
    @Override
    public String[] getArgNames() {
        return new String[] { "value" };
    }

    @Override
    public AutoautoValue call(AutoautoValue thisArg, AutoautoValue[] args) {
        if(args.length > 0 && args[0] instanceof AutoautoNumericValue) {
            int i = (int)((AutoautoNumericValue)args[0]).getDouble();
            if(i < 0) return new AutoautoNumericValue(0);

            int bit = 1 << 30;
            int numZeroBits = 0;
            while((bit & (bit ^ i)) != 0) {
                bit >>= 1;
                numZeroBits++;
            }
            return new AutoautoNumericValue(numZeroBits);
        }
        return new AutoautoUndefined();
    }
}
