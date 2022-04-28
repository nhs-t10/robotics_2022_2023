package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.prototype.string;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoBooleanValue;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoNumericValue;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoPrimitive;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoUndefined;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.NativeFunction;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.errors.ManagerSetupException;

public class StartsWithFunction extends NativeFunction {
    @Override
    public String[] getArgNames() {
        return new String[] {"searchString"};
    }

    @Override
    public AutoautoPrimitive call(AutoautoPrimitive thisArg, AutoautoPrimitive[] args) throws ManagerSetupException {
        String toSearch = thisArg.getString();

        
        if(args.length == 0) return new AutoautoBooleanValue(false);

        String search = args[0].getString();

        //find a starting index
        int index = 0;
        if(args.length >= 2 && args[1] instanceof AutoautoNumericValue) index = (int) ((AutoautoNumericValue)args[1]).value;

        //if it's negative, add the length. This lets negative numbers act as an index from the back-- e.g. -1 is the last character.
        if(index < 0) index += toSearch.length();

        //check if the index is still negative or too big. If so, make it 0.
        if(index < 0 || index >= toSearch.length()) index = 0;

        return new AutoautoBooleanValue(toSearch.startsWith(search, index));
    }
}
