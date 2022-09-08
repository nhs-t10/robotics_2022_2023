package dev.autoauto.runtime.values.prototype.string;

import dev.autoauto.runtime.values.AutoautoBooleanValue;
import dev.autoauto.runtime.values.AutoautoValue;
import dev.autoauto.runtime.NativeFunction;

public class EqualsIgnoreCaseFunction extends NativeFunction {
    @Override
    public String[] getArgNames() {
        return new String[] {"other"};
    }

    @Override
    public AutoautoValue call(AutoautoValue thisArg, AutoautoValue[] args) {
        String a = thisArg.getString();


        String b = "";
        if(args.length != 0) b = args[0].getString();

        return new AutoautoBooleanValue(a.equalsIgnoreCase(b));
    }
}
