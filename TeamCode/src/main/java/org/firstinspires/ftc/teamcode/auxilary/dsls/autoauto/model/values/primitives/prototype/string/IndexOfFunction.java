package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.prototype.string;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoNumericValue;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoPrimitive;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoString;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.NativeFunction;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.errors.ManagerSetupException;

public class IndexOfFunction extends NativeFunction {
    @Override
    public String[] getArgNames() {
        return new String[] {"search", "index"};
    }

    @Override
    public AutoautoPrimitive call(AutoautoPrimitive thisArg, AutoautoPrimitive[] args) throws ManagerSetupException {
        String toSearch = thisArg.getString();

        if(args.length == 0) return thisArg;

        String search = args[0].getString();

        int startIndex = 0;
        if(args.length >= 2 && args[1] instanceof AutoautoNumericValue) startIndex = (int) ((AutoautoNumericValue)args[1]).value;

        //if it's less than 0, add the length. This makes it act like a negative slice from Python-- `-1` means the last value.
        if(startIndex < 0) startIndex += toSearch.length();

        //if it's still out of range, return -1.
        if(startIndex < 0 || startIndex >= toSearch.length()) return new AutoautoNumericValue(-1);

        //optimization: if the search string is empty, just return 0. This allows the next optimization to *not* check for the empty string case!
        if(search.length() == 0) return new AutoautoNumericValue(0);

        //optimization: if the search string is just one letter, use `indexOf(char) instead.
        else if(search.length() == 1) return new AutoautoNumericValue(toSearch.indexOf(search.charAt(0)));

        else return new AutoautoNumericValue(toSearch.indexOf(search, startIndex));
    }
}
    