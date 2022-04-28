package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.prototype.string;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoBooleanValue;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoPrimitive;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoUndefined;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.NativeFunction;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.errors.ManagerSetupException;

import java.util.regex.PatternSyntaxException;

public class MatchesFunction extends NativeFunction {
    @Override
    public String[] getArgNames() {
        return new String[] {"regex"};
    }

    @Override
    public AutoautoPrimitive call(AutoautoPrimitive thisArg, AutoautoPrimitive[] args) throws ManagerSetupException {
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
