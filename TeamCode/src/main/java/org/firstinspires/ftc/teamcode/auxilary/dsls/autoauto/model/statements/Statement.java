package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.statements;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.AutoautoProgramElement;

public abstract class Statement implements AutoautoProgramElement {
    public String subject;
    public String predicate;

    public void loop() {}
    public void stepInit() {}
    public void init() {}
    public abstract Statement clone();
}
