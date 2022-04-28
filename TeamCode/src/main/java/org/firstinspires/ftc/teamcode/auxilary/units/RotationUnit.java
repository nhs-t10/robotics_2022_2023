package org.firstinspires.ftc.teamcode.auxilary.units;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class RotationUnit extends Unit {
    private RotationUnit(String name, String abbr, double degrees) {
        this(name, new String[] {abbr}, degrees);
    }
    private RotationUnit(String name, String[] abbreviations, double degrees) {
        super(name, abbreviations, degrees);
    }

    public final static RotationUnit DEG = new RotationUnit("Degree", new String[] {"deg", "degs"}, 1);
    public final static RotationUnit RAD = new RotationUnit("Radian", new String[] {"rad", "rads"}, 180/Math.PI);
    public final static RotationUnit ROT = new RotationUnit("Full Rotation", new String[] {"rot", "rots"}, 360);
    public final static RotationUnit TURNY = new RotationUnit("Turny", "tn", 90);
    public final static RotationUnit HALF_TURNY = new RotationUnit("Half-Turny", "hlftn", 45);

    public final static RotationUnit naturalRotationUnit = RotationUnit.DEG;

    private static RotationUnit[] unitarrcache;

    //use reflection to find all the static timeunit fields and assemble them into an array
    public static RotationUnit[] getUnits() {
        if(unitarrcache != null) return unitarrcache;

        Field[] fields = RotationUnit.class.getFields();

        ArrayList<RotationUnit> units = new ArrayList<>();
        for(Field f : fields) {
            String name = f.getName();
            boolean nameIsUppercase = name.toUpperCase().equals(name);
            if(nameIsUppercase && f.getType().equals(RotationUnit.class)) {
                try {
                    units.add((RotationUnit) f.get(null));
                } catch(Exception ignored) { }
            }
        }

        unitarrcache = units.toArray(new RotationUnit[0]);
        return unitarrcache;
    }

    public static RotationUnit forAbbreviation(String abbr) {
        String singularName = singular(abbr);

        for(RotationUnit u : getUnits()) {
            if(u.name.equalsIgnoreCase(singularName)) return u;
            for(String a : u.abbreviations) {
                if(a.equals(abbr)) return u;
            }
        }
        return null;
    }

    @Override
    public double convertToNaturalUnit(double u) {
        return convertBetween(this, naturalRotationUnit, u);
    }

    @Override
    public String getCommonAbbrev() {
        if(abbreviations.length > 0) return abbreviations[0];
        else return name;
    }

    @Override
    public Unit getNaturalUnit() {
        return naturalRotationUnit;
    }

    public static String singular(String abbr) {
        if(abbr == null) return null;
        else if(abbr.endsWith("s")) return abbr.substring(0, abbr.length() - 1);
        else return abbr;
    }

    public static double convertBetween(RotationUnit unitFrom, RotationUnit unitTo, double fromAmount) {
        return (fromAmount / unitFrom.coefficient) * unitTo.coefficient;
    }
    public static double convertBetween(RotationUnit unitFrom, RotationUnit unitTo, float fromAmount) {
        return (fromAmount / unitFrom.coefficient) * unitTo.coefficient;
    }
}