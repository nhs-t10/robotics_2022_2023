package dev.autoauto.runtime.nativefunctions.math;

import dev.autoauto.runtime.values.AutoautoNumericValue;
import dev.autoauto.runtime.values.AutoautoValue;
import dev.autoauto.runtime.values.AutoautoUndefined;
import dev.autoauto.runtime.NativeFunction;

public class log2NativeFunction extends NativeFunction {
    @Override
    public String[] getArgNames() {
        return new String[] { "value" };
    }

    private final double ln2 = Math.log(2);
    @Override
    public AutoautoValue call(AutoautoValue thisArg, AutoautoValue[] args) {
        if(args.length > 0 && args[0] instanceof AutoautoNumericValue) {
            return new AutoautoNumericValue(Math.log(((AutoautoNumericValue)args[0]).getDouble()) / ln2);
        }
        return new AutoautoUndefined();
    }
}
