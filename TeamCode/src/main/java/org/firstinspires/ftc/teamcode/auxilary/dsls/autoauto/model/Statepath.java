package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoNumericValue;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.AutoautoRuntimeVariableScope;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.AutoautoSystemVariableNames;
import org.jetbrains.annotations.NotNull;

public class Statepath implements AutoautoProgramElement {
    public final State[] states;
    private int oldCurrentState;

    Location location;
    AutoautoRuntimeVariableScope scope;

    public String name;

    public static Statepath S(State[] states, String name) {
        return new Statepath(states, name);
    }

    public Statepath(State[] states, String name) {
        this.states = states;
        this.oldCurrentState = -1;
        this.name = name;
    }

    @Override
    public AutoautoRuntimeVariableScope getScope() {
        return scope;
    }

    @Override
    public void setScope(AutoautoRuntimeVariableScope scope) {
        this.scope = scope;
        for(State s : states) s.setScope(scope);
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public void setLocation(Location location) {
        this.location = location;

        for(State s : states) s.getLocation().setStatepath(this.name);
    }

    @Override
    public Statepath clone() {
        State[] statesCloned = new State[states.length];
        for(int i = 0; i < statesCloned.length; i++) statesCloned[i] = states[i].clone();

        Statepath c = new Statepath(statesCloned, name);
        c.setLocation(location);
        return c;
    }

    public void init() {
        this.scope.systemSet(AutoautoSystemVariableNames.STATE_COUNT_OF_PREFIX + name, new AutoautoNumericValue(this.states.length));

        for(State s : this.states) s.init();
    }

    public void stepInit() {
        for(State s : this.states) s.stepInit();
    }

    public void loop() {
        int currentState = (int)((AutoautoNumericValue)(scope.get(AutoautoSystemVariableNames.STATE_NUMBER))).getFloat();

        //if steps have changed, init the new one
        if(currentState != this.oldCurrentState) {
            this.states[currentState].stepInit();
            this.oldCurrentState = currentState;
        }

        this.states[currentState].loop();
    }

    @NotNull
    public String toString() {
        StringBuilder statesStr = new StringBuilder();
        for(State state : states) {
            statesStr.append("\n    " + state.toString() + ";");
        }
        return statesStr.toString();
    }
}
