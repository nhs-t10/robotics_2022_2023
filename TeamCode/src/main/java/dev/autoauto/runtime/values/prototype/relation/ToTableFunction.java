package dev.autoauto.runtime.values.prototype.relation;

import dev.autoauto.runtime.values.AutoautoValue;
import dev.autoauto.runtime.values.AutoautoRelation;
import dev.autoauto.runtime.values.AutoautoTable;
import dev.autoauto.runtime.NativeFunction;

public class ToTableFunction extends NativeFunction {
    @Override
    public String[] getArgNames() {
        return new String[0];
    }

    @Override
    public AutoautoValue call(AutoautoValue thisValue, AutoautoValue[] args) {
        AutoautoRelation thisRelation = (AutoautoRelation) thisValue;

        AutoautoTable t = new AutoautoTable();
        t.setProperty(thisRelation.title, thisRelation.value);
        return t;
    }
}
