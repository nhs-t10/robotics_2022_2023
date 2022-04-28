package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.statements;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.Location;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.State;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.AutoautoValue;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.AutoautoRuntimeVariableScope;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.AutoautoSystemVariableNames;

public class ReturnStatement extends Statement {
    private AutoautoRuntimeVariableScope scope;
    private Location location;

    AutoautoValue value;
    State function;

    public ReturnStatement(AutoautoValue value, State function) {
        this.value = value;
        this.function = function;
    }

    @Override
    public void init() {
        value.init();
    }

    @Override
    public ReturnStatement clone() {
        ReturnStatement c = new ReturnStatement(value.clone(), function.clone());
        c.setLocation(location);
        return c;
    }

    @Override
    public void loop() {
        value.loop();

        scope.systemSet(AutoautoSystemVariableNames.RETURNED_VALUE, value.getResolvedValue());
        function.setReturnValue(value.getResolvedValue());
    }

    @Override
    public AutoautoRuntimeVariableScope getScope() {
        return scope;
    }

    @Override
    public void setScope(AutoautoRuntimeVariableScope scope) {
        this.scope = scope;
        value.setScope(scope);
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
