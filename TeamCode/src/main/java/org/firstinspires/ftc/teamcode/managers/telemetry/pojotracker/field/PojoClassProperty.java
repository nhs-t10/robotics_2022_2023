package org.firstinspires.ftc.teamcode.managers.telemetry.pojotracker.field;

import org.firstinspires.ftc.teamcode.auxilary.PaulMath;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

public abstract class PojoClassProperty {

    protected int maxDepth;
    protected int depth;

    protected Object cachedValue = null;
    protected String cachedJSON = null;

    protected String name;
    protected PojoClassProperty[] children;

    String getJSONFragment(Object value) {
        if(value == null || value != cachedValue || cachedJSON == null) {
            cachedValue = value;
            //if i'm too deep, i'm a leaf
            if (depth > maxDepth ||
                    //if i'm a leaf, i'm a leaf
                    PaulMath.isJSONable(value))
                return PaulMath.JSONify(name) + ":" + PaulMath.JSONify(value);


            invalidateChildren();
            cachedJSON = PojoClassProperty.getJSONFragmentFromProperties(children);
        }
        return cachedJSON;
    }

    protected abstract void invalidateChildren();


    //statics!
    public static String getJSONFragmentFromProperties(PojoClassProperty[] children) {
        StringBuilder r = new StringBuilder();
        for(int i = 0; i < children.length; i++) {
            String term = children[i].getJSONFragment();
            if(isValidJSONFragment(term)) {
                r.append(term);
                if(i < children.length - 1) r.append(",");
            }

        }
        String res = r.toString();

        //remove ending commas
        int trimCommaIndex = res.length();
        while(trimCommaIndex > 0 && res.charAt(trimCommaIndex - 1) == ',') trimCommaIndex--;
        if(trimCommaIndex >= 0) res = res.substring(0, trimCommaIndex);

        return res;
    }

    public abstract String getJSONFragment();

    public abstract Class getType();
    public abstract String getName();

    public abstract boolean containsKey(String key);

    public abstract void set(String key, Object value);


    public static boolean isValidJSONFragment(String fragment) {
        return fragment != null && fragment.length() > 3 && fragment.indexOf(':') != -1;
    }

    public static PojoClassProperty[] getUsableFields(Object obj, String prefix, int depth, int maxDepth) {
        return getUsableFields(obj, prefix, depth, maxDepth, true);
    }
    public static PojoClassProperty[] getUsableFields(Object obj, String prefix, int depth, int maxDepth, boolean onlyUseFieldsDefinedOnClass) {

        if(obj.getClass().isArray()) {
            return getArrayFields(obj, prefix);
        }

        Field[] fields = new Field[0];
        Method[] methods = new Method[0];
        try {
            fields = obj.getClass().getFields();
            methods = obj.getClass().getMethods();
        } catch (NoClassDefFoundError ignored) {}

        ArrayList<PojoClassProperty> usableFields = new ArrayList<PojoClassProperty>();
        int eventualLength = 0;

        for(int i = 0; i < fields.length; i++) {
            //if the user has asked to ignore inheritance, check for that
            if(onlyUseFieldsDefinedOnClass) {
                if (!fields[i].getDeclaringClass().equals(obj.getClass())) continue;
            }
            usableFields.add(new PojoClassFieldCache(fields[i], obj, prefix, depth + 1, maxDepth));
            eventualLength++;
        }
        for(int i = 0; i < methods.length; i++) {
            //if the user has asked to ignore inheritance, check for that
            if(onlyUseFieldsDefinedOnClass) {
                if (!methods[i].getDeclaringClass().equals(obj.getClass())) continue;
            }
            //no sense in including Class's methods
            if(methods[i].getDeclaringClass().equals(Class.class)) continue;
            if(methods[i].getName().equals("getClass")) continue;

            if(methods[i].getParameterTypes().length == 0 && methods[i].getName().startsWith("get")) {
                usableFields.add(new PojoClassMethodWrapper(methods[i], obj, prefix, depth + 1, maxDepth));
                eventualLength++;
            }
        }
        return usableFields.toArray(new PojoClassProperty[eventualLength]);
    }

    private static PojoClassProperty[] getArrayFields(Object obj, String prefix) {
        try {
            Object[] objArr = possiblePrimitiveToObjectArray(obj);
            PojoClassProperty[] pjpArr = new PojoClassProperty[objArr.length];
            for (int i = 0; i < objArr.length; i++) {
                pjpArr[i] = new PojoArrayElementCache(objArr, i, prefix);
            }
            return pjpArr;
        } catch(ClassCastException ignored) {
            return new PojoClassProperty[0];
        }
    }

    private static Object[] possiblePrimitiveToObjectArray(Object obj) {
        Class componentType = obj.getClass().getComponentType();
        if(componentType == null) return new Object[0];
        if(!componentType.isPrimitive()) return (Object[]) obj;

        if (int.class.equals(componentType)) return intArrToObjArr(obj);
        else if(double.class.equals(componentType)) return doubleArrToObjArr(obj);
        else if(float.class.equals(componentType)) return floatArrToObjArr(obj);
        else if(long.class.equals(componentType)) return longArrToObjArr(obj);
        else if(byte.class.equals(componentType)) return byteArrToObjArr(obj);
        else if(short.class.equals(componentType)) return shortArrToObjArr(obj);
        else if(boolean.class.equals(componentType)) return doubleArrToObjArr(obj);
        else if(char.class.equals(componentType)) return charArrToObjArr(obj);

        //if we've gotten to this point, something has gone very wrong
        return null;
    }

    private static Object[] intArrToObjArr(Object obj) {
        int[] intArr = (int[])obj;
        Integer[] objArr = new Integer[intArr.length];
        for(int i = 0; i < objArr.length; i++) {
            objArr[i] = intArr[i];
        }
        return objArr;
    }
    private static Object[] doubleArrToObjArr(Object obj) {
        double[] intArr = (double[])obj;
        Double[] objArr = new Double[intArr.length];
        for(int i = 0; i < objArr.length; i++) {
            objArr[i] = intArr[i];
        }
        return objArr;
    }
    private static Object[] floatArrToObjArr(Object obj) {
        float[] intArr = (float[])obj;
        Float[] objArr = new Float[intArr.length];
        for(int i = 0; i < objArr.length; i++) {
            objArr[i] = intArr[i];
        }
        return objArr;
    }
    private static Object[] longArrToObjArr(Object obj) {
        long[] intArr = (long[])obj;
        Long[] objArr = new Long[intArr.length];
        for(int i = 0; i < objArr.length; i++) {
            objArr[i] = intArr[i];
        }
        return objArr;
    }
    private static Object[] byteArrToObjArr(Object obj) {
        byte[] intArr = (byte[])obj;
        Byte[] objArr = new Byte[intArr.length];
        for(int i = 0; i < objArr.length; i++) {
            objArr[i] = intArr[i];
        }
        return objArr;
    }
    private static Object[] shortArrToObjArr(Object obj) {
        short[] intArr = (short[])obj;
        Short[] objArr = new Short[intArr.length];
        for(int i = 0; i < objArr.length; i++) {
            objArr[i] = intArr[i];
        }
        return objArr;
    }
    private static Object[] charArrToObjArr(Object obj) {
        char[] intArr = (char[])obj;
        Character[] objArr = new Character[intArr.length];
        for(int i = 0; i < objArr.length; i++) {
            objArr[i] = intArr[i];
        }
        return objArr;
    }

}
