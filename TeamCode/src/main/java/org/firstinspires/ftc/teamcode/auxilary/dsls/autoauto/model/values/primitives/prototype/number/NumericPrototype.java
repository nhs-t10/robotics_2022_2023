package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.prototype.number;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoBooleanValue;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.prototype.PrototypePropertyDescriptor;

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
