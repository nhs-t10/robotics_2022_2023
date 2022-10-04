package dev.autoauto.runtime.values.prototype.table;

import dev.autoauto.runtime.values.AutoautoValue;
import dev.autoauto.runtime.values.AutoautoRelation;
import dev.autoauto.runtime.values.AutoautoString;
import dev.autoauto.runtime.values.AutoautoTable;
import dev.autoauto.runtime.values.AutoautoUndefined;
import dev.autoauto.runtime.NativeFunction;

public class PropertiesFunction extends NativeFunction {
    @Override
    public String[] getArgNames() {
        return new String[0];
    }

    @Override
    public AutoautoValue call(AutoautoValue thisArg, AutoautoValue[] args) {
        AutoautoTable thisTable = (AutoautoTable) thisArg;

        String[] keys = thisTable.getEnumerableProperties();

        AutoautoRelation[] records = new AutoautoRelation[keys.length];
        for(int i = 0; i < keys.length; i++) {
            AutoautoString kS = new AutoautoString(keys[i]);
            //null-guard. This seems obscure, but it sidesteps some bugs that can come up with threads.
            AutoautoValue p = thisTable.getProperty(keys[i]);
            if(p == null) records[i] = new AutoautoRelation(kS, new AutoautoUndefined());
            else records[i] = new AutoautoRelation(kS, p);
        }
        return new AutoautoTable(records);
    }
}
