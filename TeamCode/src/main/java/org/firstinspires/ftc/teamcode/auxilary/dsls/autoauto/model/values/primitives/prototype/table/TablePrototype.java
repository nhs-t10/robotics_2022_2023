package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.prototype.table;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoBooleanValue;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoNumericValue;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.prototype.PrototypePropertyDescriptor;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.prototype.universal.NoopFunction;

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
