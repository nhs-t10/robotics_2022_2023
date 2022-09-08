package dev.autoauto.runtime.values.prototype.table;

import dev.autoauto.runtime.values.AutoautoBooleanValue;
import dev.autoauto.runtime.values.AutoautoNumericValue;
import dev.autoauto.runtime.values.prototype.PrototypePropertyDescriptor;

import java.util.HashMap;

public class TablePrototype {

    private static HashMap<String, PrototypePropertyDescriptor> map = null;

    public static HashMap<String, PrototypePropertyDescriptor> getMap() {
        if(map == null) initMap();
        return map;
    }

    private static void initMap() {
        map = new HashMap<>();
        //length has no setter
        map.put("length", new PrototypePropertyDescriptor(new AutoautoNumericValue(0)));

        map.put("push", new PrototypePropertyDescriptor(new PushFunction()));

        map.put("isTable", new PrototypePropertyDescriptor(new AutoautoBooleanValue(true)));

    }
}
