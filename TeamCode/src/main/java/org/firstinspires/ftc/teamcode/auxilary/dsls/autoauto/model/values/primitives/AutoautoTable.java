package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives;

import org.firstinspires.ftc.teamcode.auxilary.PaulMath;
import org.firstinspires.ftc.teamcode.auxilary.dsls.ParserTools;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.Location;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.operators.HasAutoautoPlusOperator;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.AutoautoPropertyBearingObject;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.AutoautoValue;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.prototype.table.TablePrototype;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.prototype.universal.UniversalPrototype;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.errors.AutoautoWhatIsGoingOnSomeoneTriedToPutANullIntoATableException;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.AutoautoRuntimeVariableScope;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AutoautoTable extends AutoautoPrimitive implements AutoautoPropertyBearingObject, HasAutoautoPlusOperator {
    private Set<String> ownProperties;

    private AutoautoRuntimeVariableScope scope;
    private Location location;

    public AutoautoTable() {
        this(new AutoautoPrimitive[0]);
    }

    public AutoautoTable(AutoautoPrimitive[] e) {
        ownProperties = new HashSet<>();
        for(int i = 0; i < e.length; i++) {
            if(e[i] instanceof AutoautoRelation) {
                String title = ((AutoautoRelation)e[i]).title.getString();
                ownProperties.add(title);
                setProperty(title, ((AutoautoRelation)e[i]).value);
            } else {
                ownProperties.add(i + "");
                setProperty(i + "", e[i]);
            }
        }
        setPrototype(TablePrototype.getMap());
    }


    @NotNull
    @Override
    public String getString() {
        StringBuilder strElems = new StringBuilder();
        for(String key : ownProperties) {
            strElems.append(key).append(" = ").append(getProperty(key)).append(", ");
        }
        String str = strElems.toString();
        //use substring() to trim off ending comma & space
        return "[" + str.substring(0, Math.max(0, str.length() - 2)) + "]";
    }

    @Override
    public AutoautoTable clone() {
        AutoautoTable c = new AutoautoTable();
        c.setLocation(location);
        c.setPrototype(this);
        for(String key : ownProperties) c.setProperty(c, getProperty(key).clone());
        return c;
    }

    public AutoautoTable combineWith(AutoautoTable other) {
        Set<String> combinedProperties = new HashSet<>();

        combinedProperties.addAll(ownProperties);
        combinedProperties.addAll(other.ownProperties);

        AutoautoTable newTable = new AutoautoTable();

        newTable.setPrototype(this);
        newTable.setPrototype(other);

        newTable.ownProperties.addAll(combinedProperties);

        return newTable;
    }

    public String toString() {
        StringBuilder strElems = new StringBuilder();
        for(String key : ownProperties) {
            strElems.append(key + " = " + getProperty(key).toString()).append(", ");
        }
        String str = strElems.toString();
        //use substring() to trim off ending comma & space
        return "[" + str.substring(0, Math.max(0, str.length() - 2)) + "]";
    }

    public String getJSONString() {
        StringBuilder strElems = new StringBuilder();
        for(String key : ownProperties) {
            strElems
                .append(PaulMath.JSONify(key))
                .append(":")
                .append(getProperty(key).getJSONString())
                .append(",");
        }
        String str = strElems.toString();
        //use substring() to trim off ending comma & space
        return "{" + str.substring(0, Math.max(0, str.length() - 1)) + "}";
    }

    @Override
    public AutoautoRuntimeVariableScope getScope() {
        return this.scope;
    }

    @Override
    public void setScope(AutoautoRuntimeVariableScope scope) {
        this.scope = scope;
        for(String key : ownProperties) {
            getProperty(key).setScope(scope);
        }
    }

    public String[] getEnumerableProperties() {
        return ownProperties.toArray(new String[0]);
    }

    @Override
    public Location getLocation() {
        return this.location;
    }

    @Override
    public void setLocation(Location location) {
        this.location = location;
    }

    public AutoautoPrimitive getProperty(String s) {
        return super.getProperty(new AutoautoString(s));
    }

    @Override
    public boolean hasProperty(AutoautoPrimitive key) {
        return ownProperties.contains(key.getString());
    }

    @Override
    public final void setProperty(AutoautoPrimitive key, AutoautoPrimitive value) {
        ownProperties.add(key.getString());
        super.setProperty(key, value);
    }
    public void setProperty(String key, AutoautoPrimitive value) {
        setProperty(new AutoautoString(key), value);
    }
    public void setPropertyWithoutOwning(String key, AutoautoPrimitive value) {
        super.setProperty(new AutoautoString(key), value);
    }

    public static AutoautoTable fromJSONObject(String str) {
        AutoautoTable table = new AutoautoTable();

        String nonBracketString = str;
        if(str.startsWith("{")) nonBracketString = str.substring(1, str.length() - 1);

        String[] elems = ParserTools.groupAwareSplit(nonBracketString, ',');

        for(int i = 0; i < elems.length; i++) {
            int keyIndex = elems[i].indexOf(':');
            String key = elems[i].substring(0, keyIndex);
            String value = elems[i].substring(keyIndex + 1);
            table.setProperty(AutoautoValue.fromJSON(key), AutoautoValue.fromJSON(value));
        }

        return table;
    }

    public static AutoautoTable fromJSONArray(String str) {
        AutoautoTable table = new AutoautoTable();

        String nonBracketString = str;
        if(str.startsWith("[")) nonBracketString = str.substring(1, str.length() - 1);

        String[] elems = ParserTools.groupAwareSplit(nonBracketString, ',');

        for(int i = 0; i < elems.length; i++) {
            table.setProperty(new AutoautoNumericValue(i), AutoautoValue.fromJSON(elems[i]));
        }

        return table;
    }

    @Override
    public int dataWidth() {
        return 4;
    }

    public boolean isEmpty() {
        return ownProperties.isEmpty();
    }
    public int size() {
        return ownProperties.size();
    }

    @Override
    public AutoautoPrimitive opPlus(AutoautoPrimitive other, boolean otherIsLeft) {
        if(other instanceof AutoautoRelation) {
            AutoautoTable cl = clone();
            cl.setProperty(((AutoautoRelation) other).title, ((AutoautoRelation) other).value);
            return cl;
        } else if(other instanceof AutoautoTable) {
            if(otherIsLeft) return ((AutoautoTable) other).combineWith(this);
            else return this.combineWith((AutoautoTable) other);
        } else {
            return this;
        }
    }
}
