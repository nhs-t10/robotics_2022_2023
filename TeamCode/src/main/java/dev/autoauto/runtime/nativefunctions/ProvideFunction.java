package dev.autoauto.runtime.nativefunctions;

import dev.autoauto.runtime.values.AutoautoValue;
import dev.autoauto.runtime.values.AutoautoUndefined;
import dev.autoauto.runtime.AutoautoSystemVariableNames;
import dev.autoauto.runtime.NativeFunction;

public class ProvideFunction extends NativeFunction {
    @Override
    public String[] getArgNames() {
        return new String[] { "value" };
    }

    @Override
    public AutoautoValue call(AutoautoValue thisArg, AutoautoValue[] args) {
        AutoautoValue export = null;
        if(args.length > 0) export = args[0];



        getScope().getRoot().systemSet(AutoautoSystemVariableNames.EXPORTS, export);

        return new AutoautoUndefined();
    }
}
