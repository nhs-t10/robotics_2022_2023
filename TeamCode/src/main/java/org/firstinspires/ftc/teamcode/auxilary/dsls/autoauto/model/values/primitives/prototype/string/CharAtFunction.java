package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.prototype.string;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoNumericValue;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoPrimitive;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoString;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoUndefined;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.NativeFunction;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.errors.ManagerSetupException;

public class CharAtFunction extends NativeFunction {
    @Override
    public String[] getArgNames() {
        return new String[] {"char"};
    }

    @Override
    public AutoautoPrimitive call(AutoautoPrimitive thisArg, AutoautoPrimitive[] args) throws ManagerSetupException {
        String toSearch = thisArg.getString();

        int index = 0;
        if(args.length >= 1 && args[0] instanceof AutoautoNumericValue) index = (int) ((AutoautoNumericValue)args[0]).value;

        //if it's negative, add the length. This lets negative numbers act as an index from the back-- e.g. -1 is the last character.
        if(index < 0) index += toSearch.length();

        //check if the index is still negative or too big. If so, return undefined.
        if(index < 0 || index >= toSearch.length()) return new AutoautoUndefined();

        else return new AutoautoString("" + toSearch.charAt(index));
    }
}
    