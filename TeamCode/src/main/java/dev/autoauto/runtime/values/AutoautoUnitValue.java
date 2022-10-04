package dev.autoauto.runtime.values;


import androidx.annotation.NonNull;

import dev.autoauto.model.Location;
import dev.autoauto.runtime.AutoautoRuntimeVariableScope;
import org.jetbrains.annotations.NotNull;

public class AutoautoUnitValue extends AutoautoNumericValue {
    //Attributes
    Location location;
    AutoautoRuntimeVariableScope scope;
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

    public static enum UnitType { TIME, DISTANCE, UNKNOWN, ROTATION };

    public UnitType unitType;
    public double baseAmount;
    public String unit;

    public AutoautoUnitValue(double baseAmount, String unit) {
        super(baseAmount);
        this.baseAmount = baseAmount;
        this.unit = unit;
    }

    //Methods
    @NotNull
    public String getString() {
        return this.baseAmount + this.unit;
    }

    public AutoautoUnitValue clone() {
        AutoautoUnitValue c = new AutoautoUnitValue(baseAmount, unitType == UnitType.TIME ? "ms" : "ticks");
        c.setLocation(location);
        return c;
    }

    @NonNull
    public String toString() {
        return this.baseAmount + this.unit;
    }
}