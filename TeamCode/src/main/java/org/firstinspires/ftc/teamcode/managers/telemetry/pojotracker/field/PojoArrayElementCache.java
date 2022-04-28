package org.firstinspires.ftc.teamcode.managers.telemetry.pojotracker.field;

public class PojoArrayElementCache extends PojoClassProperty {
    int index;
    Class type;
    Object[] array;



    public PojoArrayElementCache(Object[] array, int index, String prefix) {
        //null checks! Don't we love them!
        if(array == null) throw new IllegalArgumentException("given array is null");
        if(index < 0 || index >= array.length) throw new IllegalArgumentException("out-of-bounds");

        this.name = prefix + "[" + index + "]";
        this.array = array;
        this.type = array.getClass();
        this.index = index;
    }

    @Override
    public String getJSONFragment() {
        return getJSONFragment(array[index]);
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

    public void set(String key, Object value) throws IllegalArgumentException {

    }

}
