package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.statements;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.Location;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.AutoautoValue;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.AutoautoRuntimeVariableScope;
import org.jetbrains.annotations.NotNull;

public class ValueStatement extends Statement {
    public AutoautoValue function;

    AutoautoRuntimeVariableScope scope;
    Location location;

    public static ValueStatement F(AutoautoValue f) {
        return new ValueStatement(f);
    }

    public ValueStatement(AutoautoValue f) {
        this.function = f;
    }

    @NotNull
    public String toString() {
        return this.function.toString();
    }

    public void loop() {
        function.loop();
    }

    @Override
    public void init() {
        this.function.init();
    }

    @Override
    public ValueStatement clone() {
        ValueStatement c = new ValueStatement(function.clone());
        c.setLocation(location);
        return c;
    }

    @Override
    public void stepInit() {
    }

    @Override
    public AutoautoRuntimeVariableScope getScope() {
        return scope;
    }

    @Override
    public void setScope(AutoautoRuntimeVariableScope scope) {
        this.scope = scope;
        function.setScope(scope);
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
