package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.nativefunctions.math;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoNumericValue;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoPrimitive;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoUndefined;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.errors.ManagerSetupException;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.NativeFunction;

public class clz32NativeFunction extends NativeFunction {
    @Override
    public String[] getArgNames() {
        return new String[] { "value" };
    }

    @Override
    public AutoautoPrimitive call(AutoautoPrimitive thisArg, AutoautoPrimitive[] args) throws ManagerSetupException {
        if(args.length > 0 && args[0] instanceof AutoautoNumericValue) {
            int i = (int)((AutoautoNumericValue)args[0]).getFloat();
            if(i < 0) return new AutoautoNumericValue(0);

            int bit = 1 << 30;
            int numZeroBits = 0;
            while((bit & (bit ^ i)) != 0) {
                bit >>= 1;
                numZeroBits++;
            }
            return new AutoautoNumericValue(numZeroBits);
        }
        return new AutoautoUndefined();
    }
}
