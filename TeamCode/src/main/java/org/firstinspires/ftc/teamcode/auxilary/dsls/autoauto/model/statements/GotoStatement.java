package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.statements;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.Location;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoString;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.AutoautoValue;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.AutoautoRuntimeVariableScope;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.AutoautoSystemVariableNames;

public class GotoStatement extends Statement {
    AutoautoValue gotoPath;

    private AutoautoRuntimeVariableScope scope;
    private Location location;

    public static GotoStatement G(String path) {
        return new GotoStatement(path);
    }
    public static GotoStatement G(AutoautoValue path) {
        return new GotoStatement(path);
    }

    public GotoStatement(String path) {
        this.gotoPath = new AutoautoString(path);
    }

    public GotoStatement(AutoautoValue path) {
        this.gotoPath = path;
    }

    public void loop() {
        gotoPath.loop();
        scope.systemSet(AutoautoSystemVariableNames.STATEPATH_NAME, gotoPath.getResolvedValue());
    }

    @Override
    public GotoStatement clone() {
        GotoStatement c = new GotoStatement(gotoPath.getResolvedValue());
        c.setLocation(location);
        return c;
    }

    public String toString() {
        return "goto " + this.gotoPath;
    }

    @Override
    public AutoautoRuntimeVariableScope getScope() {
        return scope;
    }

    @Override
    public void setScope(AutoautoRuntimeVariableScope scope) {
        this.gotoPath.setScope(scope);
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
