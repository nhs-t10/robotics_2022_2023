package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.prototype.string;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.prototype.PrototypePropertyDescriptor;

import java.util.HashMap;

public class StringPrototype {
    private static HashMap<String, PrototypePropertyDescriptor> map;

    public static HashMap<String, PrototypePropertyDescriptor> getMap() {
        if(map == null) initMap();
        return map;
    }

    private static void initMap() {
        map = new HashMap<>();
        map.put("length", new PrototypePropertyDescriptor(new LengthGetter(), null));

        map.put("getBytes", new PrototypePropertyDescriptor(new GetBytesFunction()));
        map.put("toLowerCase", new PrototypePropertyDescriptor(new ToLowerCaseFunction()));
        map.put("toUpperCase", new PrototypePropertyDescriptor(new ToUpperCaseFunction()));
        map.put("trim", new PrototypePropertyDescriptor(new TrimFunction()));
        map.put("charAt", new PrototypePropertyDescriptor(new CharAtFunction()));
        map.put("indexOf", new PrototypePropertyDescriptor(new IndexOfFunction()));
        map.put("endsWith", new PrototypePropertyDescriptor(new EndsWithFunction()));
        map.put("startsWith", new PrototypePropertyDescriptor(new StartsWithFunction()));
        map.put("matches", new PrototypePropertyDescriptor(new MatchesFunction()));
        map.put("equalsIgnoreCase", new PrototypePropertyDescriptor(new EqualsIgnoreCaseFunction()));
        map.put("split", new PrototypePropertyDescriptor(new SplitFunction()));
        map.put("repeat", new PrototypePropertyDescriptor(new RepeatFunction()));
        map.put("replace", new PrototypePropertyDescriptor(new ReplaceFunction()));

        map.put("substring", new PrototypePropertyDescriptor(new SubstringFunction()));
        map.put("slice", new PrototypePropertyDescriptor(new SubstringFunction()));
    }
}
