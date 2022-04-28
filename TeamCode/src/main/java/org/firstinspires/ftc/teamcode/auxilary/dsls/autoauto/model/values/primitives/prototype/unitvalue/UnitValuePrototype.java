package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.prototype.unitvalue;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.prototype.PrototypePropertyDescriptor;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.prototype.universal.NoopFunction;

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
