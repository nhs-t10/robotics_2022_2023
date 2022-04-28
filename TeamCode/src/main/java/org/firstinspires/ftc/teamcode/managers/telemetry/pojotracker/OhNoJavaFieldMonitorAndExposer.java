package org.firstinspires.ftc.teamcode.managers.telemetry.pojotracker;


import org.firstinspires.ftc.teamcode.managers.telemetry.pojotracker.field.PojoClassFieldCache;
import org.firstinspires.ftc.teamcode.managers.telemetry.pojotracker.field.PojoClassMethodWrapper;
import org.firstinspires.ftc.teamcode.managers.telemetry.pojotracker.field.PojoClassProperty;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class OhNoJavaFieldMonitorAndExposer {

    private int depth;
    private int maxDepth;
    private Object object;

    private PojoClassProperty[] fields;

    public OhNoJavaFieldMonitorAndExposer() {}

    public OhNoJavaFieldMonitorAndExposer(Object object) {
        this(object, Integer.MAX_VALUE, false);
    }
    public OhNoJavaFieldMonitorAndExposer(Object object, boolean ignoreInheritedFields) {
        this(object, Integer.MAX_VALUE, ignoreInheritedFields);
    }
    public OhNoJavaFieldMonitorAndExposer(Object object, int maxDepth) {
        this(object, maxDepth, false);
    }

    public OhNoJavaFieldMonitorAndExposer(Object object, int maxDepth, boolean ignoreInheritedFields) {
        this.object = object;
        this.depth = 0;
        this.maxDepth = maxDepth;

        this.fields = PojoClassProperty.getUsableFields(object, "", depth, maxDepth, ignoreInheritedFields);
    }

    public boolean hasKey(String name) {
        for (PojoClassProperty field : fields) {
            if (field.containsKey(name)) {
                return true;
            }
        }
        return false;
    }

    public Class<?> getTypeOfField(String name) {
        for (PojoClassProperty field : fields) {
            if (field.containsKey(name)) {
                return field.getType();
            }
        }
        return null;
    }

    public void set(String name, Object value) {
        for(PojoClassProperty field : fields) {
            if(field.containsKey(name)) {
                try {
                    field.set(name, value);
                } catch(Exception ignored) { }
                break;
            }
        }
    }

    public String getJSON() {
        if(fields == null || object == null) return "null";
        return"{" + PojoClassProperty.getJSONFragmentFromProperties(fields) +
                "}";
    }

    public void parseAndSet(String key, String value) {
        Class type = getTypeOfField(key);

        if(type.isPrimitive()) {
                if(type.equals(Integer.class)) set(key, Integer.valueOf(value));
                else if(type.equals(Boolean.class)) set(key, Boolean.valueOf(value));
                else if(type.equals(Byte.class)) set(key, Byte.valueOf(value));
                else if(type.equals(Long.class)) set(key, Long.valueOf(value));
                else if(type.equals(Float.class)) set(key, Float.valueOf(value));
                else if(type.equals(Double.class)) set(key, Double.valueOf(value));
                else if(type.equals(Character.class)) set(key, value.charAt(0));
                else if(type.equals(Short.class)) set(key, Short.valueOf(value));
        }
        //if not, it *has* to be a String
        //or null, which can be handled automagically
        else {
            set(key, value);
        }
    }
}
