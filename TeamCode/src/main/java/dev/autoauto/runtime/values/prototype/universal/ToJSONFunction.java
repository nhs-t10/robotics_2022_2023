package dev.autoauto.runtime.values.prototype.universal;

import dev.autoauto.runtime.values.AutoautoValue;
import dev.autoauto.runtime.values.AutoautoString;
import dev.autoauto.runtime.NativeFunction;

public class ToJSONFunction extends NativeFunction {
    @Override
    public String[] getArgNames() {
        return new String[] {};
    }

    @Override
    public AutoautoValue call(AutoautoValue thisArg, AutoautoValue[] args) {
        return new AutoautoString(thisArg.getJSONString());
    }
}
