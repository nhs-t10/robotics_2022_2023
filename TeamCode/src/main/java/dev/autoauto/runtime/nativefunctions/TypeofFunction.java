package dev.autoauto.runtime.nativefunctions;

import dev.autoauto.runtime.values.AutoautoBooleanValue;
import dev.autoauto.runtime.values.AutoautoCallableValue;
import dev.autoauto.runtime.values.AutoautoNumericValue;
import dev.autoauto.runtime.values.AutoautoValue;
import dev.autoauto.runtime.values.AutoautoString;
import dev.autoauto.runtime.values.AutoautoUndefined;
import dev.autoauto.runtime.values.AutoautoUnitValue;
import dev.autoauto.runtime.values.AutoautoRelation;
import dev.autoauto.runtime.NativeFunction;

public class TypeofFunction extends NativeFunction {
    public String name = "length";
    public int argCount = 1;

    public TypeofFunction() {
    }

    @Override
    public String[] getArgNames() {
        return new String[0];
    }

    @Override
    public AutoautoValue call(AutoautoValue thisArg, AutoautoValue[] args) {
        if(args.length == 0 || args[0] == null) return new AutoautoUndefined();

        AutoautoValue p = args[0];

        if(p instanceof AutoautoUndefined && ((AutoautoUndefined) p).source == AutoautoUndefined.NONEXISTENT_VARIABLE) return new AutoautoUndefined();

        return new AutoautoString(type(p));
    }

    private String type(AutoautoValue p) {
        if(p instanceof AutoautoUnitValue) return "unit";
        if(p instanceof AutoautoNumericValue) return "numeric";
        if(p instanceof AutoautoString) return "string";
        if(p instanceof AutoautoBooleanValue) return "boolean";
        if(p instanceof AutoautoCallableValue) return "function";
        if(p instanceof AutoautoUndefined) return "undefined";
        if(p instanceof AutoautoRelation) return type(((AutoautoRelation)p).value);
        return "table";
    }
}