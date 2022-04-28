package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.AutoautoRuntimeVariableScope;

public interface AutoautoProgramElement {
    AutoautoRuntimeVariableScope getScope();

    void setScope(AutoautoRuntimeVariableScope scope);

    Location getLocation();

    void setLocation(Location location);

    AutoautoProgramElement clone();

    default String formatStack() {
        Location location = getLocation();
        return "\n\tat " + location.fileName + ":" + location.line + ":" + location.col + "\n\tat " + location.statepath + ", state " + location.stateNumber;
    }
}
