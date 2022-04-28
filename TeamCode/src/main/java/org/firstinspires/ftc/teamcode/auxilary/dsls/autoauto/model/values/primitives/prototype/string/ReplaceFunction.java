package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.prototype.string;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoPrimitive;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoString;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.NativeFunction;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.errors.ManagerSetupException;

import java.util.regex.PatternSyntaxException;

public class ReplaceFunction extends NativeFunction {
    @Override
    public String[] getArgNames() {
        return new String[] {"toReplace","replaceWith"};
    }

    @Override
    public AutoautoPrimitive call(AutoautoPrimitive thisValue, AutoautoPrimitive[] args) throws ManagerSetupException {
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
