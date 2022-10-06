package dev.autoauto.runtime.values.prototype.string;

import dev.autoauto.runtime.values.AutoautoNumericValue;
import dev.autoauto.runtime.values.AutoautoValue;
import dev.autoauto.runtime.values.AutoautoString;
import dev.autoauto.runtime.values.AutoautoUndefined;
import dev.autoauto.runtime.NativeFunction;

public class CharAtFunction extends NativeFunction {
    @Override
    public String[] getArgNames() {
        return new String[] {"char"};
    }

    @Override
    public AutoautoValue call(AutoautoValue thisArg, AutoautoValue[] args) {
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
    