package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives;

import org.firstinspires.ftc.teamcode.auxilary.PaulMath;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.Location;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.State;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.AutoautoCallableValue;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.AutoautoRuntimeVariableScope;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.AutoautoSystemVariableNames;
import org.jetbrains.annotations.NotNull;

public class AutoautoFunction extends AutoautoPrimitive implements AutoautoCallableValue {
    private State body;
    private AutoautoRuntimeVariableScope scope;
    private Location location;
    private String[] argNames;
    private AutoautoPrimitive[] defaultArgValues;

    public AutoautoFunction(State body) {
        this(body, new String[0]);
    }

    public AutoautoFunction(State body, String[] argNames) {
        this(body, argNames, new AutoautoPrimitive[0]);
    }
    public AutoautoFunction(State body, String[] argNames, AutoautoPrimitive[] defaultArgValues) {
        this.body = body;
        this.argNames = argNames;
        this.defaultArgValues = defaultArgValues;
    }

    @NotNull
    @Override
    public String getString() {
        return "<anonymous function>() {" + body.toString() + "}";
    }

    @Override
    public AutoautoFunction clone() {
        AutoautoFunction c = new AutoautoFunction(body.clone());
        return c;
    }

    @Override
    public AutoautoRuntimeVariableScope getScope() {
        return scope;
    }

    @Override
    public void setScope(AutoautoRuntimeVariableScope scope) {
        this.scope = scope;
        body.setScope(scope);
    }

    @Override
    public Location getLocation() {
        return null;
    }

    @Override
    public void setLocation(Location location) {
        this.location = location;
        if(this.body.location == null) this.body.setLocation(this.location);
    }

    @Override
    public int dataWidth() {
        return 5;
    }

    @Override
    public String[] getArgNames() {
        return argNames;
    }

    @Override
    public AutoautoPrimitive call(AutoautoPrimitive thisValue, AutoautoPrimitive[] args) {
        AutoautoRuntimeVariableScope callScope = new AutoautoRuntimeVariableScope(scope.getRoot());
        AutoautoNumericValue actualStateNumber = (AutoautoNumericValue) scope.get(AutoautoSystemVariableNames.STATE_NUMBER);
        AutoautoString actualStatepath = (AutoautoString) scope.get(AutoautoSystemVariableNames.STATEPATH_NAME);

        if(argNames != null) {
            for (int i = 0; i < argNames.length; i++) {
                if (i < args.length && !(args[i] instanceof AutoautoUndefined)) {
                    callScope.systemSet(argNames[i], args[i]);
                } else {
                    if(defaultArgValues == null || defaultArgValues[i] == null) callScope.systemSet(argNames[i], new AutoautoUndefined());
                    else callScope.systemSet(argNames[i], defaultArgValues[i]);
                }
            }
        }

        State callState = body.clone();
        callState.setScope(callScope);

        AutoautoTable arglist = new AutoautoTable(args);
        callScope.systemSet(AutoautoSystemVariableNames.FUNCTION_ARGUMENTS_NAME, arglist.clone());

        callScope.systemSet(AutoautoSystemVariableNames.THIS, thisValue);

        callScope.systemSet(AutoautoSystemVariableNames.STATE_NUMBER, new AutoautoNumericValue(body.location.stateNumber));
        callScope.systemSet(AutoautoSystemVariableNames.STATEPATH_NAME, new AutoautoString(body.location.statepath));

        callState.init();
        callState.stepInit();
        callState.loop();

        callScope.systemSet(AutoautoSystemVariableNames.STATE_NUMBER, actualStateNumber);
        callScope.systemSet(AutoautoSystemVariableNames.STATEPATH_NAME, actualStatepath);

        callScope.systemRemove(AutoautoSystemVariableNames.RETURNED_VALUE);
        callScope.systemRemove(AutoautoSystemVariableNames.FUNCTION_ARGUMENTS_NAME);

        return callState.getReturnValue();
    }

    @Override
    public String getJSONString() {
        return PaulMath.JSONify("[function Function]");
    }
}
