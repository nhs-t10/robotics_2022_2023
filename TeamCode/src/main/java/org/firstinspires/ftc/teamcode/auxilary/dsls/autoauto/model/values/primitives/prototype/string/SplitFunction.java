package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.prototype.string;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoBooleanValue;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoNumericValue;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoPrimitive;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoString;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoTable;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.NativeFunction;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.errors.ManagerSetupException;

import java.util.regex.PatternSyntaxException;

public class SplitFunction extends NativeFunction {
    @Override
    public String[] getArgNames() {
        return new String[] {"splitBy", "maxSplits"};
    }

    @Override
    public AutoautoPrimitive call(AutoautoPrimitive thisArg, AutoautoPrimitive[] args) throws ManagerSetupException {
        String toSplit = thisArg.getString();

        //default the splitBy parameter to ","
        String splitBy = ",";
        if(args.length != 0) splitBy = args[0].getString();

        //find a starting index
        int splitLimit = 0;
        if(args.length >= 2 && args[1] instanceof AutoautoNumericValue) splitLimit = (int) ((AutoautoNumericValue)args[1]).value;


        String[] words = null;
        try {
            //check if the index is 0 or too big to bother with, split without caring for it.
            if (splitLimit < 0 || splitLimit >= toSplit.length()) words = toSplit.split(splitBy);
            else words = toSplit.split(splitBy, splitLimit);
        } catch(PatternSyntaxException ignored) {}

        //if the regex was improper, act like someone tried to split by nothing.
        if(words == null) return new AutoautoTable(new AutoautoPrimitive[] { thisArg });

        //make the string array into an equivalent AutoautoString array
        AutoautoPrimitive[] wordPrims = new AutoautoPrimitive[words.length];
        for(int i = 0; i < wordPrims.length; i++) wordPrims[i] = new AutoautoString(words[i]);

        return new AutoautoTable(wordPrims);
    }
}
