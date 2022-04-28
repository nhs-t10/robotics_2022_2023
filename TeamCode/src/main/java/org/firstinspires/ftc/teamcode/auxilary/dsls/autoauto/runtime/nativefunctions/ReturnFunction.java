package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.nativefunctions;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoPrimitive;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoUndefined;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.AutoautoSystemVariableNames;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.NativeFunction;

public class ReturnFunction extends NativeFunction {
    public String name = "defun";
    public int argCount = 1;


    public ReturnFunction() {
    }

    @Override
    public String[] getArgNames() {
        return new String[0];
    }

    @Override
    public AutoautoPrimitive call(AutoautoPrimitive thisArg, AutoautoPrimitive[] args) {
        AutoautoPrimitive returnedVal = args.length == 0 ? new AutoautoUndefined() : args[0];

        getScope().systemSet(AutoautoSystemVariableNames.RETURNED_VALUE, returnedVal);

        return returnedVal;
    }
}