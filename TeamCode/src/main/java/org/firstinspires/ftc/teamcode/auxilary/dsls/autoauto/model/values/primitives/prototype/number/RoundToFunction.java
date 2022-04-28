package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.prototype.number;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoNumericValue;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoPrimitive;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoUndefined;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.NativeFunction;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.errors.ManagerSetupException;

public class RoundToFunction extends NativeFunction {
    @Override
    public String[] getArgNames() {
        return new String[] {"Precision"};
    }

    @Override
    public AutoautoPrimitive call(AutoautoPrimitive thisArg, AutoautoPrimitive[] args) throws ManagerSetupException {
        AutoautoNumericValue thisNumber = (AutoautoNumericValue) thisArg;

        //if the user didn't give us a number, just return the same old value
        if(args.length == 0 || !(args[0] instanceof AutoautoNumericValue)) return thisNumber;

        AutoautoNumericValue rounder = (AutoautoNumericValue) args[0];

        float t = thisNumber.value;
        float r = rounder.value;

        double p = Math.pow(10, -(int)r);

        //mutate a clone. This makes it work properly for UnitValues without blowing up the numericvalue code.
        AutoautoNumericValue newNum = (AutoautoNumericValue) thisArg.clone();

        newNum.value = (float) (Math.round(t / p) * p);

        return newNum;
    }
}
