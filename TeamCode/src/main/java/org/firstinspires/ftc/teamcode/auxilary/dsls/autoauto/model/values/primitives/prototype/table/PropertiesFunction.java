package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.prototype.table;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoNumericValue;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoPrimitive;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoRelation;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoString;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoTable;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoUndefined;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.NativeFunction;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.errors.ManagerSetupException;

import java.util.Set;

public class PropertiesFunction extends NativeFunction {
    @Override
    public String[] getArgNames() {
        return new String[0];
    }

    @Override
    public AutoautoPrimitive call(AutoautoPrimitive thisArg, AutoautoPrimitive[] args) throws ManagerSetupException {
        AutoautoTable thisTable = (AutoautoTable) thisArg;

        String[] keys = thisTable.getEnumerableProperties();

        AutoautoRelation[] records = new AutoautoRelation[keys.length];
        for(int i = 0; i < keys.length; i++) {
            AutoautoString kS = new AutoautoString(keys[i]);
            //null-guard. This seems obscure, but it sidesteps some bugs that can come up with threads.
            AutoautoPrimitive p = thisTable.getProperty(keys[i]);
            if(p == null) records[i] = new AutoautoRelation(kS, new AutoautoUndefined());
            else records[i] = new AutoautoRelation(kS, p);
        }
        return new AutoautoTable(records);
    }
}
