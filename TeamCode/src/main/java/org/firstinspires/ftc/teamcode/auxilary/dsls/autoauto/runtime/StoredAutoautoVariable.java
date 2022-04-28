package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoPrimitive;

public class StoredAutoautoVariable {
    public AutoautoPrimitive value;
    public boolean readOnly;
    public boolean systemManaged;
    
    public StoredAutoautoVariable(AutoautoPrimitive v) {
        this.value = v;
    }
}
