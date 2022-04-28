package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.prototype.string;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoBooleanValue;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoNumericValue;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoPrimitive;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoString;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.NativeFunction;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.errors.ManagerSetupException;

public class SubstringFunction extends NativeFunction {

    @Override
    public String[] getArgNames() {
        return new String[] {"startIndex", "endIndex"};
    }

    @Override
    public AutoautoPrimitive call(AutoautoPrimitive thisArg, AutoautoPrimitive[] args) throws ManagerSetupException {
        String toSlice = thisArg.getString();

        //if the string is empty, don't do all the expensive processing. Just return it!
        if(toSlice.length() == 0) return thisArg;

        //if there's no indexes specified, just return the string, unchanged.
        if(args.length == 0) return thisArg;

        //find a starting index
        int startIndex = 0;
        if(args.length >= 1 && args[0] instanceof AutoautoNumericValue) startIndex = (int) ((AutoautoNumericValue)args[0]).value;

        //if the start index is negative, add the length. This lets negative numbers act as an index from the back-- e.g. -1 is the last character.
        if(startIndex < 0) startIndex += toSlice.length();
        //if the start index is STILL negative, make it 0
        if(startIndex < 0) startIndex = 0;

        //if it's >= the length, we don't need to calculate an end-position! Just return an empty string.
        if(startIndex >= toSlice.length()) return new AutoautoString("");

        //find an ending index
        int endIndex = toSlice.length();
        if(args.length >= 2 && args[1] instanceof AutoautoNumericValue) endIndex = (int) ((AutoautoNumericValue)args[1]).value;

        //if the end index is negative, add the length. This lets negative numbers act as an index from the back-- e.g. -1 is the last character.
        if(endIndex < 0) endIndex += toSlice.length();

        //if the end index is STILL negative, return an empty string.
        if(endIndex <= 0) return new AutoautoString("");

        //if the end is after the string, bring it back to avoid an IndexOutOfBoundsException.
        if(endIndex > toSlice.length()) endIndex = toSlice.length();

        //if the starting index is more than the end index, return an empty string
        if(startIndex > endIndex) return new AutoautoString("");

        return new AutoautoString(toSlice.substring(startIndex, endIndex));
    }
}
