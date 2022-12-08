package dev.autoauto.runtime.values.prototype.relation;

import dev.autoauto.runtime.values.AutoautoValue;
import dev.autoauto.runtime.values.AutoautoRelation;
import dev.autoauto.runtime.NativeFunction;

public class ValueGetter extends NativeFunction {
    @Override
    public String[] getArgNames() {
        return new String[0];
    }

    @Override
    public AutoautoValue call(AutoautoValue thisValue, AutoautoValue[] args) {
        return ((AutoautoRelation)thisValue).value;
    }
}
