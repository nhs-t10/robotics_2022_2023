package dev.autoauto.runtime.values.prototype.relation;

import dev.autoauto.runtime.values.AutoautoValue;
import dev.autoauto.runtime.values.AutoautoRelation;
import dev.autoauto.runtime.values.AutoautoTable;
import dev.autoauto.runtime.NativeFunction;

public class ToArrayFunction extends NativeFunction {
    @Override
    public String[] getArgNames() {
        return new String[0];
    }

    @Override
    public AutoautoValue call(AutoautoValue thisValue, AutoautoValue[] args) {
        AutoautoRelation thisRelation = (AutoautoRelation) thisValue;
        return new AutoautoTable(new AutoautoValue[] { thisRelation.title, thisRelation.value });
    }
}
