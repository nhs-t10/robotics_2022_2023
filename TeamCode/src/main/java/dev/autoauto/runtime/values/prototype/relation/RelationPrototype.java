package dev.autoauto.runtime.values.prototype.relation;

import dev.autoauto.runtime.values.AutoautoBooleanValue;
import dev.autoauto.runtime.values.prototype.PrototypePropertyDescriptor;
import dev.autoauto.runtime.values.prototype.universal.NoopFunction;

import java.util.HashMap;

public class RelationPrototype {
    private static HashMap<String, PrototypePropertyDescriptor> map;

    public static HashMap<String, PrototypePropertyDescriptor> getMap() {
        if(map == null) initMap();
        return map;
    }

    private static void initMap() {
        map = new HashMap<>();

        map.put("title", new PrototypePropertyDescriptor(new TitleGetter(), new NoopFunction()));
        map.put("value", new PrototypePropertyDescriptor(new ValueGetter(), new NoopFunction()));

        map.put("toArray", new PrototypePropertyDescriptor(new ToArrayFunction()));
        map.put("toTable", new PrototypePropertyDescriptor(new ToTableFunction()));

        map.put("isRelation", new PrototypePropertyDescriptor(new AutoautoBooleanValue(true)));
    }
}
