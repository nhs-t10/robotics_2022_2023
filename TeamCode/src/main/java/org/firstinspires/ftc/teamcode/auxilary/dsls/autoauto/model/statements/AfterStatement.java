package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.statements;

import org.firstinspires.ftc.teamcode.auxilary.RobotTime;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.Location;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.AutoautoCallableValue;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.AutoautoValue;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoNumericValue;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoPrimitive;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoUndefined;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoUnitValue;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.AutoautoRuntimeVariableScope;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.AutoautoSystemVariableNames;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.errors.AutoautoArgumentException;
import org.firstinspires.ftc.teamcode.auxilary.units.DistanceUnit;
import org.firstinspires.ftc.teamcode.auxilary.units.RotationUnit;
import org.jetbrains.annotations.NotNull;

public class AfterStatement extends Statement {
    //Attributes
    AutoautoValue wait;
    Statement action;

    Location location;
    AutoautoRuntimeVariableScope scope;

    private long stepStartTime = 0;
    private float stepStartTick;

    private AutoautoUnitValue waitWithUnit;
    private boolean restartDeltaNextLoop;

    //Constructors
    public static AfterStatement W (AutoautoValue wait, Statement action) {
        return new AfterStatement(wait, action);
    }

    public AfterStatement(AutoautoValue wait, Statement action) {
        this.wait = wait;
        this.action = action;
    }

    AutoautoCallableValue getTicks;
    boolean isDistanceUnit;

    @NotNull
    public String toString() {
        return "after " + this.wait.toString() + " " + this.action.toString();
    }

    @Override
    public void init() {
        action.init();
        wait.init();
    }

    @Override
    public AfterStatement clone() {
        AfterStatement c = new AfterStatement(wait.clone(), action.clone());
        c.setLocation(location);
        return c;
    }

    public final static String[][] unitMethodMapping = new String[][] {
            {"D", DistanceUnit.naturalDistanceUnit.name, AutoautoSystemVariableNames.GET_CENTIMETERS_FUNCTION_NAME},
            {"D", RotationUnit.naturalRotationUnit.name, AutoautoSystemVariableNames.GET_DEGREES_FUNCTION_NAME},
            {"D", "ticks", "getTicks"}
    };

    private boolean unitExistsInMethodMapping(String unit) {
        for(String[] s : unitMethodMapping) {
            if(s[1].equals(unit)) return true;
        }
        return false;
    }
    private String getUnitMethodFromMapping(String unit) {
        for(String[] s : unitMethodMapping) {
            if(s[1].equals(unit)) return s[2];
        }
        throw new IllegalArgumentException("No such unit `" + unit + "` in mapping.");
    }
    private boolean checkUnitIsDistance(String unit) {
        for (String[] s : unitMethodMapping) {
            if (s[1].equals(unit)) {
                return s[0].equals("D");
            }
        }
        return false;
    }

    @Override
    public void stepInit() {
        if(scope.get(AutoautoSystemVariableNames.COMPATFLAG_AFTER_TIMESTART_AT_START_OF_STATE) != null) internalStoreStartingStep();
        else restartDeltaNextLoop = true;
    }

    private void internalStoreStartingStep() {
        restartDeltaNextLoop = false;
        AutoautoPrimitive waitResolved = wait.getResolvedValue();

        if(!(waitResolved instanceof AutoautoUnitValue)) throw new AutoautoArgumentException("attempted to wait on a non-unit value");

        waitWithUnit = (AutoautoUnitValue) waitResolved;

        if(unitExistsInMethodMapping(waitWithUnit.unit) && waitWithUnit.unitType == AutoautoUnitValue.UnitType.UNKNOWN) waitWithUnit.unitType = AutoautoUnitValue.UnitType.DISTANCE;

        switch (waitWithUnit.unitType) {
            case TIME:
                this.stepStartTime = RobotTime.currentTimeMillis();
                break;
            case DISTANCE:
            case ROTATION:
                getTicks = (AutoautoCallableValue) scope.get(getUnitMethodFromMapping(waitWithUnit.unit));
                this.stepStartTick = ((AutoautoNumericValue)getTicks.call(new AutoautoUndefined(), new AutoautoPrimitive[0])).getFloat();
                break;
        }
    }

    public void loop() {
        wait.loop();
        if(restartDeltaNextLoop) internalStoreStartingStep();

        if(waitWithUnit != null) {
            switch (waitWithUnit.unitType) {
                case TIME:
                    if (RobotTime.currentTimeMillis() >= stepStartTime + waitWithUnit.baseAmount)
                        action.loop();
                    break;
                case DISTANCE:
                case ROTATION:
                    double targetDifference = waitWithUnit.baseAmount;
                    float referPoint = stepStartTick;
                    float currentPosition = ((AutoautoNumericValue) getTicks.call(new AutoautoUndefined(), new AutoautoPrimitive[0])).getFloat();

                    if (Math.abs(currentPosition - referPoint) >= Math.abs(targetDifference))
                        action.loop();
                    break;
            }
        } else throw new IllegalStateException("waitWithUnit should never be null since checked for previously in stepInit");
    }

    @Override
    public AutoautoRuntimeVariableScope getScope() {
        return scope;
    }

    @Override
    public void setScope(AutoautoRuntimeVariableScope scope) {
        this.scope = scope;
        action.setScope(scope);
        wait.setScope(scope);
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