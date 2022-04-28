package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives;


import androidx.annotation.NonNull;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.Location;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.AutoautoRuntimeVariableScope;
import org.firstinspires.ftc.teamcode.auxilary.units.DistanceUnit;
import org.firstinspires.ftc.teamcode.auxilary.units.RotationUnit;
import org.firstinspires.ftc.teamcode.auxilary.units.TimeUnit;
import org.firstinspires.ftc.teamcode.managers.feature.FeatureManager;
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

    public static AutoautoUnitValue E(double baseAmount, String unit) {
        return new AutoautoUnitValue(baseAmount, unit);
    }

    public static AutoautoUnitValue E(double baseAmount, String unit, boolean attemptConversion) {
        return new AutoautoUnitValue(baseAmount, unit, attemptConversion);
    }

    public AutoautoUnitValue(double baseAmount, String unit) {
        this(baseAmount, unit, false);
    }

    public AutoautoUnitValue(double baseAmount, String unit, boolean attemptConversion) {
        super(baseAmount);
        this.baseAmount = baseAmount;
        this.unit = unit;

        if(attemptConversion) {
            TimeUnit timeUnit = TimeUnit.forAbbreviation(unit);
            DistanceUnit distanceUnit = DistanceUnit.forAbbreviation(unit);
            RotationUnit rotationUnit = RotationUnit.forAbbreviation(unit);

            if (timeUnit != null) {
                this.baseAmount = TimeUnit.convertBetween(timeUnit, TimeUnit.naturalTimeUnit, baseAmount);
                this.unit = TimeUnit.naturalTimeUnit.name;
                this.unitType = UnitType.TIME;
            } else if (distanceUnit != null) {
                this.baseAmount = DistanceUnit.convertBetween(distanceUnit, DistanceUnit.naturalDistanceUnit, baseAmount);
                this.unit = DistanceUnit.naturalDistanceUnit.name;
                this.unitType = UnitType.DISTANCE;
            } else if (rotationUnit != null) {
                this.baseAmount = RotationUnit.convertBetween(rotationUnit, RotationUnit.naturalRotationUnit, baseAmount);
                this.unit = RotationUnit.naturalRotationUnit.name;
                this.unitType = UnitType.ROTATION;
            } else {
                this.unitType = UnitType.UNKNOWN;
                FeatureManager.logger.warn("Unknown unit `" + unit + "`; please use a distance, time, or rotational unit listed under the auxilary.units package.");
            }
            super.value = (float) this.baseAmount;
        }
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