package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.statements;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.Location;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.AutoautoValue;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoNumericValue;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoPrimitive;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.AutoautoRuntimeVariableScope;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.AutoautoSystemVariableNames;
import org.jetbrains.annotations.NotNull;

public class SkipStatement extends Statement {
    private AutoautoValue delta;
    private AutoautoRuntimeVariableScope scope;
    private Location location;

    int stateCount = -1;

    public static SkipStatement N(AutoautoValue v) {
        return new SkipStatement(v);
    }

    public SkipStatement(AutoautoValue v) {
        this.delta = v;
    }
    public void loop() {
        AutoautoPrimitive delRes = this.delta.getResolvedValue();

        int delta = 0;
        if(delRes instanceof AutoautoNumericValue) delta = (int) ((AutoautoNumericValue) delRes).value;

        int currentState = (int)((AutoautoNumericValue)scope.get(AutoautoSystemVariableNames.STATE_NUMBER)).getFloat();

        int nextState = (currentState + delta) % stateCount;
        if(nextState < 0) nextState = stateCount + nextState;

        scope.systemSet(AutoautoSystemVariableNames.STATE_NUMBER, new AutoautoNumericValue(nextState));
    }

    @Override
    public void init() {
        this.stateCount = (int)((AutoautoNumericValue)scope.get(AutoautoSystemVariableNames.STATE_COUNT_OF_PREFIX + location.statepath)).getFloat();
    }

    @Override
    public SkipStatement clone() {
        SkipStatement c = new SkipStatement(delta.clone());
        c.setLocation(location);
        return c;
    }

    @NotNull
    public String toString() {
        return "skip " + delta;
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
