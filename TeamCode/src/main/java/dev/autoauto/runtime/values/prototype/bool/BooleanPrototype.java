package dev.autoauto.runtime.values.prototype.bool;

import dev.autoauto.runtime.values.AutoautoBooleanValue;
import dev.autoauto.runtime.values.prototype.PrototypePropertyDescriptor;

import java.util.HashMap;

public class BooleanPrototype {
    private static HashMap<String, PrototypePropertyDescriptor> map;

    public static HashMap<String, PrototypePropertyDescriptor> getMap() {
        if(map == null) initMap();
        return map;
    }

    private static void initMap() {
        map = new HashMap<>();

        map.put("isBoolean", new PrototypePropertyDescriptor(new AutoautoBooleanValue(true)));
    }
}
