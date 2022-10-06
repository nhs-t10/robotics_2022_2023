package dev.autoauto.runtime.values.prototype.string;

import dev.autoauto.runtime.values.AutoautoBooleanValue;
import dev.autoauto.runtime.values.AutoautoValue;
import dev.autoauto.runtime.values.AutoautoUndefined;
import dev.autoauto.runtime.NativeFunction;

import java.util.regex.PatternSyntaxException;

public class MatchesFunction extends NativeFunction {
    @Override
    public String[] getArgNames() {
        return new String[] {"regex"};
    }

    @Override
    public AutoautoValue call(AutoautoValue thisArg, AutoautoValue[] args) {
        String toSearch = thisArg.getString();


        if(args.length == 0) return new AutoautoBooleanValue(false);

        String regex = args[0].getString();

        try {
            return new AutoautoBooleanValue(toSearch.matches(regex));
        } catch(PatternSyntaxException ignored) {
            //if the regex was incorrect, return undefined
            return new AutoautoUndefined();
        }
    }
}
