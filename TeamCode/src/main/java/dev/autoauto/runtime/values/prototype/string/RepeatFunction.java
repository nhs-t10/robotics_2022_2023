package dev.autoauto.runtime.values.prototype.string;

import dev.autoauto.runtime.values.AutoautoNumericValue;
import dev.autoauto.runtime.values.AutoautoValue;
import dev.autoauto.runtime.values.AutoautoString;
import dev.autoauto.runtime.NativeFunction;

public class RepeatFunction extends NativeFunction {
    @Override
    public String[] getArgNames() {
        return new String[] {"repeatCount"};
    }

    @Override
    public AutoautoValue call(AutoautoValue thisArg, AutoautoValue[] args) {
        String toRepeat = thisArg.getString();

        int repCount = 0;
        if(args.length >= 1 && args[0] instanceof AutoautoNumericValue) repCount = (int) ((AutoautoNumericValue)args[0]).value;

        StringBuilder repeated = new StringBuilder();

        for(int i = 0; i < repCount; i++) repeated.append(toRepeat);

        return new AutoautoString(repeated.toString());
    }
}
