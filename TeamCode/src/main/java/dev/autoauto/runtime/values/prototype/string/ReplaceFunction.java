package dev.autoauto.runtime.values.prototype.string;

import dev.autoauto.runtime.values.AutoautoValue;
import dev.autoauto.runtime.values.AutoautoString;
import dev.autoauto.runtime.NativeFunction;

import java.util.regex.PatternSyntaxException;

public class ReplaceFunction extends NativeFunction {
    @Override
    public String[] getArgNames() {
        return new String[] {"toReplace","replaceWith"};
    }

    @Override
    public AutoautoValue call(AutoautoValue thisValue, AutoautoValue[] args) {
        String replaceOn = thisValue.getString();

        //if no replacement is specified, return unchanged.
        if(args.length == 0) return thisValue;
        String regex = args[0].getString();

        //if there's not a replacement specified, use `undefined`
        String replaceWith = "undefined";
        if(args.length >= 2) replaceWith = args[1].getString();

        try {
            return new AutoautoString(replaceOn.replaceAll(regex, replaceWith));
        } catch(PatternSyntaxException ignored) {
            //if the regex syntax was invalid, return unchanged
            return thisValue;
        }
    }
}
