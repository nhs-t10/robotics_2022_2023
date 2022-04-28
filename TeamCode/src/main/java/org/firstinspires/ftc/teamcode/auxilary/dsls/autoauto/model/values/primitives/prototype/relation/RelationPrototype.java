package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.prototype.relation;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoBooleanValue;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.prototype.PrototypePropertyDescriptor;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.prototype.universal.NoopFunction;

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
