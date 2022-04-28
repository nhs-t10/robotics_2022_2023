package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.statements;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.Location;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.AutoautoRuntimeVariableScope;
import org.jetbrains.annotations.NotNull;

public class PassStatement extends Statement {

    public static PassStatement Y() {
        return new PassStatement();
    }

    private Location location;
    private AutoautoRuntimeVariableScope scope;

    public PassStatement() {

    }
    public void loop() {}

    @Override
    public void init() {

    }

    @Override
    public PassStatement clone() {
        PassStatement c = new PassStatement();
        c.setLocation(location);
        return c;
    }

    @NotNull
    public String toString() {
        return "pass";
    }

    @Override
    public AutoautoRuntimeVariableScope getScope() {
        return scope;
    }

    @Override
    public void setScope(AutoautoRuntimeVariableScope scope) {
        this.scope = scope;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public void setLocation(Location location) {
        this.location = location;
    }
}
