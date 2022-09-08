package dev.autoauto.runtime.values.prototype.number;

import dev.autoauto.runtime.values.AutoautoBooleanValue;
import dev.autoauto.runtime.values.prototype.PrototypePropertyDescriptor;

import java.util.HashMap;

public class NumericPrototype {
    private static HashMap<String, PrototypePropertyDescriptor> map;

    public static HashMap<String, PrototypePropertyDescriptor> getMap() {
        if(map == null) initMap();
        return map;
    }

    private static void initMap() {
        map = new HashMap<>();
        map.put("roundTo", new PrototypePropertyDescriptor(new RoundToFunction()));
        map.put("clip", new PrototypePropertyDescriptor(new ClipFunction()));

        map.put("isNumericValue", new PrototypePropertyDescriptor(new AutoautoBooleanValue(true)));

    }
}
