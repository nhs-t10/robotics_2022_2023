package dev.autoauto.runtime;

import dev.autoauto.runtime.values.AutoautoValue;

public class StoredAutoautoVariable {
    public AutoautoValue value;
    public boolean readOnly;
    public boolean systemManaged;
    
    public StoredAutoautoVariable(AutoautoValue v) {
        this.value = v;
    }
}
