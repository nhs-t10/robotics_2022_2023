package dev.autoauto.runtime.nativefunctions;

import dev.autoauto.runtime.values.AutoautoValue;
import dev.autoauto.runtime.values.AutoautoUndefined;
import dev.autoauto.runtime.AutoautoSystemVariableNames;
import dev.autoauto.runtime.NativeFunction;

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
    public AutoautoValue call(AutoautoValue thisArg, AutoautoValue[] args) {
        AutoautoValue returnedVal = args.length == 0 ? new AutoautoUndefined() : args[0];

        getScope().systemSet(AutoautoSystemVariableNames.RETURNED_VALUE, returnedVal);

        return returnedVal;
    }
}