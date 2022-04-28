package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.prototype.string;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoBooleanValue;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoNumericValue;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoPrimitive;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoString;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoUndefined;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.NativeFunction;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.errors.ManagerSetupException;

public class RepeatFunction extends NativeFunction {
    @Override
    public String[] getArgNames() {
        return new String[] {"repeatCount"};
    }

    @Override
    public AutoautoPrimitive call(AutoautoPrimitive thisArg, AutoautoPrimitive[] args) throws ManagerSetupException {
        String toRepeat = thisArg.getString();

        int repCount = 0;
        if(args.length >= 1 && args[0] instanceof AutoautoNumericValue) repCount = (int) ((AutoautoNumericValue)args[0]).value;

        StringBuilder repeated = new StringBuilder();

        for(int i = 0; i < repCount; i++) repeated.append(toRepeat);

        return new AutoautoString(repeated.toString());
    }
}
