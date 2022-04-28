package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.AutoautoProgramElement;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoNumericValue;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoPrimitive;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoString;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoTable;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoUndefined;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.errors.AutoautoNameException;
import org.jetbrains.annotations.NotNull;

public abstract class AutoautoValue implements AutoautoProgramElement {

    @NotNull
    public abstract AutoautoPrimitive getResolvedValue();

    public abstract void init();
    public abstract void loop() throws AutoautoNameException;

    @NotNull
    public abstract String getString();

    public abstract AutoautoValue clone();

    protected AutoautoString asString() {
        if(this instanceof AutoautoString) return (AutoautoString)this;
        else return new AutoautoString(this.getString());
    }
    public boolean equals(@NotNull AutoautoValue other) {
        return other.getString().equals(this.getString());
    }
    public int hashCode() {
        return getString().hashCode();
    }


    public static AutoautoPrimitive fromJSON(String str) {
        String trim = str.trim();

        if(trim.startsWith("\"")) AutoautoString.fromJSON(trim);
        else if(trim.startsWith("{")) AutoautoTable.fromJSONObject(trim);
        else if(trim.startsWith("[")) AutoautoTable.fromJSONArray(trim);
        else if(trim.equals("null")) return new AutoautoUndefined();
        else return new AutoautoNumericValue(Float.parseFloat(trim));

        return new AutoautoUndefined();
    }
}
