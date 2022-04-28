package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.statements;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.Location;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.State;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoBooleanValue;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.AutoautoValue;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.AutoautoRuntimeVariableScope;

public class IfStatement extends Statement {
    AutoautoValue check;
    State subject;
    State otherwise;
    private AutoautoRuntimeVariableScope scope;
    private Location location;

    public static IfStatement I (AutoautoValue v, State s, State o) { return new IfStatement(v,s,o); }
    public static IfStatement I (AutoautoValue v, Statement s, State o) { return new IfStatement(v,s,o); }
    public static IfStatement I (AutoautoValue v, Statement s, Statement o) { return new IfStatement(v,s,o); }
    public static IfStatement I (AutoautoValue v, State s, Statement o) { return new IfStatement(v,s,o); }


    public IfStatement(AutoautoValue v, State s, State o) {
        this.check = v;
        this.subject = s;
        this.otherwise = o;
    }

    public IfStatement(AutoautoValue v, Statement s, State o) {
        this(v, new State(new Statement[] {s}), o);
    }

    public IfStatement(AutoautoValue v, Statement s, Statement o) {
        this(v, new State(new Statement[] {s}), new State(new Statement[] {o}));
    }

    public IfStatement(AutoautoValue v, State s, Statement o) {
        this(v, s, new State(new Statement[] {o}));
    }

    public void init() {
        check.init();
        subject.init();
        otherwise.init();
    }
    public void stepInit() {
        subject.stepInit();
        otherwise.stepInit();
    }

    @Override
    public IfStatement clone() {
        IfStatement c = new IfStatement(check.clone(), subject.clone(), otherwise.clone());
        c.setLocation(location);
        return c;
    }

    public void loop() {
        check.loop();
        if(AutoautoBooleanValue.isTruthy(check.getResolvedValue())) {
            subject.loop();
        } else {
            otherwise.loop();
        }
    }
    public String toString() {
        return "if (" + check.toString() + ") " + subject.toString() + " else " + otherwise.clone();
    }

    @Override
    public AutoautoRuntimeVariableScope getScope() {
        return scope;
    }

    @Override
    public void setScope(AutoautoRuntimeVariableScope scope) {
        this.scope = scope;
        check.setScope(scope);
        subject.setScope(scope);
        otherwise.setScope(scope);
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public void setLocation(Location location) {
        this.location = location;
        if(this.subject.location == null) this.subject.setLocation(location.clone());
        if(this.otherwise.location == null) this.otherwise.setLocation(location.clone());
    }
}
