package dev.autoauto.runtime.values.prototype.string;

import dev.autoauto.runtime.values.AutoautoBooleanValue;
import dev.autoauto.runtime.values.AutoautoNumericValue;
import dev.autoauto.runtime.values.AutoautoValue;
import dev.autoauto.runtime.NativeFunction;

public class StartsWithFunction extends NativeFunction {
    @Override
    public String[] getArgNames() {
        return new String[] {"searchString"};
    }

    @Override
    public AutoautoValue call(AutoautoValue thisArg, AutoautoValue[] args) {
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
