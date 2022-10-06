package dev.autoauto.runtime.nativefunctions.math;

import dev.autoauto.runtime.values.AutoautoNumericValue;
import dev.autoauto.runtime.values.AutoautoValue;
import dev.autoauto.runtime.NativeFunction;

public class randomNativeFunction extends NativeFunction {
    @Override
    public String[] getArgNames() {
        return new String[] {  };
    }

    @Override
    public AutoautoValue call(AutoautoValue thisArg, AutoautoValue[] args) {
        return new AutoautoNumericValue(Math.random());
    }
}
