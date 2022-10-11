package dev.autoauto.runtime.values.prototype.table;

import dev.autoauto.runtime.values.AutoautoNumericValue;
import dev.autoauto.runtime.values.AutoautoValue;
import dev.autoauto.runtime.values.AutoautoTable;
import dev.autoauto.runtime.NativeFunction;

public class PushFunction extends NativeFunction {
    @Override
    public String[] getArgNames() {
        return new String[0];
    }

    @Override
    public AutoautoValue call(AutoautoValue thisArg, AutoautoValue[] args) {
        AutoautoTable thisTable = (AutoautoTable) thisArg;
        AutoautoValue lenP = thisTable.getProperty("length");
        if(!(lenP instanceof AutoautoNumericValue)) return lenP;

        int len = (int) ((AutoautoNumericValue)lenP).value;

        for(int i = 0; i < args.length; i++) {
            thisTable.setProperty(new AutoautoNumericValue(len + i), args[i]);
        }
        AutoautoNumericValue newLen = new AutoautoNumericValue(len + args.length);
        thisTable.setPropertyWithoutOwning("length", newLen);
        return newLen;
    }
}
