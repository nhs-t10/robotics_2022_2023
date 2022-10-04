package dev.autoauto.runtime.values.prototype.string;

import dev.autoauto.runtime.values.AutoautoBooleanValue;
import dev.autoauto.runtime.values.AutoautoValue;
import dev.autoauto.runtime.NativeFunction;

public class EndsWithFunction extends NativeFunction {
    @Override
    public String[] getArgNames() {
        return new String[] {"searchString"};
    }

    @Override
    public AutoautoValue call(AutoautoValue thisArg, AutoautoValue[] args) {
        String toSearch = thisArg.getString();


        if(args.length == 0) return new AutoautoBooleanValue(false);

        String search = args[0].getString();

        return new AutoautoBooleanValue(toSearch.endsWith(search));
    }
}
