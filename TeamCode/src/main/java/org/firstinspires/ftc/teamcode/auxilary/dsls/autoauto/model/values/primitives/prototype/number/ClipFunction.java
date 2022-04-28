package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.prototype.number;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoNumericValue;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoPrimitive;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.NativeFunction;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.errors.ManagerSetupException;

public class ClipFunction extends NativeFunction {
    @Override
    public String[] getArgNames() {
        return new String[] {"Minimum", "Maximum"};
    }

    @Override
    public AutoautoPrimitive call(AutoautoPrimitive thisArg, AutoautoPrimitive[] args) throws ManagerSetupException {
        AutoautoNumericValue thisNumber = (AutoautoNumericValue) thisArg;

        //if the user didn't give us a number, just return the same old value
        if(args.length == 0) return thisNumber;

        float minN = args[0].castToNumber().value;

        float t = thisNumber.value;
        float mN = Math.max(minN, t);

        if(args.length == 1) return new AutoautoNumericValue(mN);

        float maxN = args[1].castToNumber().value;

        //mutate a clone. This makes it work properly for UnitValues without blowing up the numericvalue code.
        AutoautoNumericValue newNum = (AutoautoNumericValue) thisArg.clone();

        newNum.value = Math.min(mN, maxN);

        return newNum;
    }
}

