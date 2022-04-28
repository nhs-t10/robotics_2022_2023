package org.firstinspires.ftc.teamcode.managers.telemetry.pojotracker.field;

import org.firstinspires.ftc.teamcode.auxilary.PaulMath;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PojoClassMethodWrapper extends PojoClassProperty {
    Method method;
    Class type;
    Object object;


    public PojoClassMethodWrapper(Method method, Object object, String prefix, int depth, int maxDepth) {
        if(object == null) throw new IllegalArgumentException("given Object is null");
        if(method == null) throw new IllegalArgumentException("given Field is null");
        if(!method.getDeclaringClass().isAssignableFrom(object.getClass())) throw new IllegalArgumentException("given Field isn't declared on given Object");

        if(prefix.equals("")) this.name = method.getName() + "()";
        else this.name = prefix + "." + method.getName() + "()";

        this.method = method;
        this.type = method.getReturnType();
        this.object = object;

        this.depth = depth;
        this.maxDepth = maxDepth;
    }

    public String getJSONFragment() {
        try {
            Object value = method.invoke(object);
            return getJSONFragment(value);
        } catch(IllegalAccessException | InvocationTargetException err) {
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
        else if(key.indexOf('[') == -1) return key.equals(name);
        else return key.substring(0, key.indexOf('[')).equals(name);
    }

    public void set(String key, Object value) {
        throw new IllegalArgumentException("Attempt to set returned value");
    }
}
