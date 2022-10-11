package dev.autoauto.model.bytecode;

import dev.autoauto.runtime.values.AutoautoNumericValue;

public class loadint_Bytecode extends loadconst_Bytecode {
    public loadint_Bytecode(int i) {
        super(new AutoautoNumericValue(i));
    }
}
