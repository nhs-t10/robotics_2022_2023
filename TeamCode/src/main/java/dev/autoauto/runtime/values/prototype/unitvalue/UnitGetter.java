package dev.autoauto.runtime.values.prototype.unitvalue;

import dev.autoauto.runtime.values.AutoautoValue;
import dev.autoauto.runtime.values.AutoautoString;
import dev.autoauto.runtime.values.AutoautoUnitValue;
import dev.autoauto.runtime.NativeFunction;

public class UnitGetter extends NativeFunction {
    @Override
    public String[] getArgNames() {
        return new String[0];
    }

    @Override
    public AutoautoValue call(AutoautoValue thisValue, AutoautoValue[] args) {
        AutoautoUnitValue thisUnitvalue = (AutoautoUnitValue) thisValue;
        return new AutoautoString(thisUnitvalue.unit.toString());
    }
}
