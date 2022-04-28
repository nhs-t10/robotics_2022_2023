package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.bytecode;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoNumericValue;

public class loadint_Bytecode extends loadconst_Bytecode {
    public loadint_Bytecode(int i) {
        super(new AutoautoNumericValue(i));
    }
}
