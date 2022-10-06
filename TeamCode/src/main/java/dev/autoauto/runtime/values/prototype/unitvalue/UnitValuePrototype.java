package dev.autoauto.runtime.values.prototype.unitvalue;

import dev.autoauto.runtime.values.prototype.PrototypePropertyDescriptor;
import dev.autoauto.runtime.values.prototype.universal.NoopFunction;

import java.util.HashMap;

public class UnitValuePrototype {
    private static HashMap<String, PrototypePropertyDescriptor> map;

    public static HashMap<String, PrototypePropertyDescriptor> getMap() {
        if(map == null) initMap();
        return map;
    }

    private static void initMap() {
        map = new HashMap<>();

        map.put("unit", new PrototypePropertyDescriptor(new UnitGetter(), new NoopFunction()));
        map.put("unit", new PrototypePropertyDescriptor(new UnitGetter(), new NoopFunction()));
    }
}
