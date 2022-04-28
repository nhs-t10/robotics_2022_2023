package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.nativefunctions.math;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoNumericValue;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoPrimitive;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.errors.ManagerSetupException;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.NativeFunction;

public class randomNativeFunction extends NativeFunction {
    @Override
    public String[] getArgNames() {
        return new String[] {  };
    }

    @Override
    public AutoautoPrimitive call(AutoautoPrimitive thisArg, AutoautoPrimitive[] args) throws ManagerSetupException {
        return new AutoautoNumericValue(Math.random());
    }
}
