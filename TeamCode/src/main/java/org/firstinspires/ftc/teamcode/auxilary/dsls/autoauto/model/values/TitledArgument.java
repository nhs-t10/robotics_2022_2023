package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.Location;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoString;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoRelation;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.AutoautoRuntimeVariableScope;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.errors.AutoautoNameException;
import org.jetbrains.annotations.NotNull;

public class TitledArgument extends AutoautoValue {
    public AutoautoValue title;
    public AutoautoValue value;
    private AutoautoRuntimeVariableScope scope;
    private Location location;

    public static TitledArgument V(AutoautoValue title, AutoautoValue value) {
        return new TitledArgument(title, value);
    }
    public static TitledArgument V(String title, AutoautoValue value) {
        return new TitledArgument(new AutoautoString(title), value);
    }

    public TitledArgument(AutoautoValue title, AutoautoValue value) {
        if(title instanceof VariableReference) this.title = new AutoautoString(((VariableReference) title).name);
        else this.title = title;

        this.value = value;
    }

    @NotNull
    @Override
    public AutoautoRelation getResolvedValue() {
        return new AutoautoRelation(title.getResolvedValue(), value.getResolvedValue());
    }

    @Override
    public void init() {}

    @Override
    public void loop() throws AutoautoNameException {
        this.title.loop();
        this.value.loop();
    }


    @NotNull
    @Override
    public String getString() {
        return value.getString();
    }

    @Override
    public AutoautoRuntimeVariableScope getScope() {
        return scope;
    }

    @Override
    public void setScope(AutoautoRuntimeVariableScope scope) {
        this.scope = scope;
        title.setScope(scope);
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

    @Override
    public AutoautoValue clone() {
        return new TitledArgument(title.clone(), value.clone());
    }

    public String toString() {
        return title.toString() + " = " + value.toString();
    }
}
