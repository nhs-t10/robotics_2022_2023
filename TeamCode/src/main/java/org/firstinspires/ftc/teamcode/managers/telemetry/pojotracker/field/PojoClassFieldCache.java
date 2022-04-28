package org.firstinspires.ftc.teamcode.managers.telemetry.pojotracker.field;

import org.firstinspires.ftc.teamcode.auxilary.PaulMath;

import java.lang.reflect.Field;

public class PojoClassFieldCache extends PojoClassProperty {
    Field field;
    Class type;
    Object object;


    public PojoClassFieldCache(Field field, Object object, String prefix, int depth, int maxDepth) {
        //null checks! Don't we love them!
        if(object == null) throw new IllegalArgumentException("given Object is null");
        if(field == null) throw new IllegalArgumentException("given Field is null");
        if(!field.getDeclaringClass().isAssignableFrom(object.getClass())) throw new IllegalArgumentException("given Field isn't declared on given Object");

        this.name = field.getName();
        if(!prefix.equals("")) this.name = prefix + "." + this.name;
        this.field = field;
        this.type = field.getType();
        this.object = object;

        this.depth = depth;
        this.maxDepth = maxDepth;
    }
    public String getJSONFragment() {
        try {
            return getJSONFragment(field.get(object));
        } catch(IllegalAccessException err) {
            return "\"err\":" + PaulMath.JSONify(err.toString());
        }
    }

    protected void invalidateChildren() {
        this.children = PojoClassProperty.getUsableFields(cachedValue, name, depth, maxDepth);
    }

    public Class getType() {
        return type;
    }
    public String getName() {
        return name;
    }

    public boolean containsKey(String key) {
        if(key == null) return false;
        else return key.startsWith(name);
    }

    public void set(String key, Object value) throws IllegalArgumentException {

    }

}
