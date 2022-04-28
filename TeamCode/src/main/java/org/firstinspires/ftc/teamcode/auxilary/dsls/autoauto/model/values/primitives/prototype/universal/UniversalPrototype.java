package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.prototype.universal;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.prototype.PrototypePropertyDescriptor;

import java.util.HashMap;

public class UniversalPrototype {
    private static HashMap<String, PrototypePropertyDescriptor> map;

    public static HashMap<String, PrototypePropertyDescriptor> getMap() {
        if(map == null) initMap();
        return map;
    }

    private static void initMap() {
        map = new HashMap<>();
        map.put("toString", new PrototypePropertyDescriptor(new ToStringFunction()));
        map.put("toJSON", new PrototypePropertyDescriptor(new ToJSONFunction()));
    }

}
