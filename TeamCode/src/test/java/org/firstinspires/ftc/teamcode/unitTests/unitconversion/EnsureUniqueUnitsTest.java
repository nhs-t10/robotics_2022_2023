package org.firstinspires.ftc.teamcode.unitTests.unitconversion;

import org.firstinspires.ftc.teamcode.auxilary.units.DistanceUnit;
import org.firstinspires.ftc.teamcode.auxilary.units.RotationUnit;
import org.firstinspires.ftc.teamcode.auxilary.units.TimeUnit;
import org.firstinspires.ftc.teamcode.auxilary.units.Unit;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class EnsureUniqueUnitsTest {


    @Test
    public void test() {
        DistanceUnit[] distanceUnits = DistanceUnit.getUnits();
        TimeUnit[] timeUnits = TimeUnit.getUnits();
        RotationUnit[] rotationUnits = RotationUnit.getUnits();

        HashMap<String, Unit> existing = new HashMap<>();

        for(Unit u : Unit.getAllUnits()) {
            if(existing.containsKey(u.name)) reportProblem(u.name, u, existing);
            existing.put(u.name, u);
            for(String abb : u.abbreviations) {
                if(existing.containsKey(abb)) reportProblem(abb, u, existing);
                existing.put(abb, u);
            }
        }
    }

    public void reportProblem(String unitNameSpec, Unit u, HashMap<String, Unit> existing) {
        if(existing.containsKey(unitNameSpec)) {
            Unit existingUnit = existing.get(unitNameSpec);
            assertNotNull(existingUnit);
            assertEquals(u.toString(), u.toString() + "; " + existingUnit.toString());
        }
    }

}
