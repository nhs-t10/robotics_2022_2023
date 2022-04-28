package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.prototype.relation;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoPrimitive;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoRelation;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoTable;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.NativeFunction;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.errors.ManagerSetupException;

public class ToArrayFunction extends NativeFunction {
    @Override
    public String[] getArgNames() {
        return new String[0];
    }

    @Override
    public AutoautoPrimitive call(AutoautoPrimitive thisValue, AutoautoPrimitive[] args) throws ManagerSetupException {
        AutoautoRelation thisRelation = (AutoautoRelation) thisValue;
        return new AutoautoTable(new AutoautoPrimitive[] { thisRelation.title, thisRelation.value });
    }
}
