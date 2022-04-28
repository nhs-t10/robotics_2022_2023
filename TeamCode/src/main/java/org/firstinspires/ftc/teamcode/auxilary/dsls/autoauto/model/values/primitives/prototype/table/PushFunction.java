package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.prototype.table;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoNumericValue;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoPrimitive;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoString;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoTable;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoUndefined;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.NativeFunction;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.errors.ManagerSetupException;

public class PushFunction extends NativeFunction {
    @Override
    public String[] getArgNames() {
        return new String[0];
    }

    @Override
    public AutoautoPrimitive call(AutoautoPrimitive thisArg, AutoautoPrimitive[] args) throws ManagerSetupException {
        AutoautoTable thisTable = (AutoautoTable) thisArg;
        AutoautoPrimitive lenP = thisTable.getProperty("length");
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
