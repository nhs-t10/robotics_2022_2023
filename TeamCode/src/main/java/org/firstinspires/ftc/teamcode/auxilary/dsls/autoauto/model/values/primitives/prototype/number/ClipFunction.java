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

        double minN = args[0].castToNumber().value;

        double t = thisNumber.value;
        double mN = Math.max(minN, t);

        if(args.length == 1) return new AutoautoNumericValue(mN);

        double maxN = args[1].castToNumber().value;

        return new AutoautoNumericValue(Math.min(mN, maxN));
    }
}

