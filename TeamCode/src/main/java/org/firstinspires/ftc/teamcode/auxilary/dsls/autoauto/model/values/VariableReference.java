package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.Location;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoPrimitive;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoUndefined;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.AutoautoRuntimeVariableScope;
import org.jetbrains.annotations.NotNull;

public class VariableReference extends AutoautoValue {
    String name;

    public AutoautoPrimitive resolvedValue;
    private Location location;
    private AutoautoRuntimeVariableScope scope;

    public static VariableReference H (String n) {
        return new VariableReference(n);
    }

    public VariableReference(String n) {
        this.name = n;
    }

    public String getName() {
        return name;
    }

    public void init() {}

    public void loop() {
        this.resolvedValue = scope.get(this.name);
        if(this.resolvedValue == null) this.resolvedValue = new AutoautoUndefined(AutoautoUndefined.NONEXISTENT_VARIABLE);
    }

    @NotNull
    public AutoautoPrimitive getResolvedValue() {
        return resolvedValue;
    }

    @NotNull
    @Override
    public String getString() {
        return resolvedValue.getString();
    }

    @Override
    public VariableReference clone() {
        VariableReference c = new VariableReference(name);
        c.setLocation(location);
        return c;
    }

    public String toString() {
        return this.name;
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
