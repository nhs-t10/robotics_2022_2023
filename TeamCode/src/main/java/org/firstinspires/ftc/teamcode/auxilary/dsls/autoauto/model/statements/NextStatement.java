package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.statements;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoNumericValue;

public class NextStatement extends SkipStatement {

    public static NextStatement N() {
        return new NextStatement();
    }

    public NextStatement() {
        super(new AutoautoNumericValue(1f));
    }
    public String toString() {
        return "next";
    }
}
