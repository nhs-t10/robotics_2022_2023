package org.firstinspires.ftc.teamcode.auxilary.units;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class TimeUnit extends Unit {

    private TimeUnit(String name, String abbr, double milliseconds, String pluralName) {
        this(name, new String[] {abbr}, milliseconds, pluralName);
    }
    private TimeUnit(String name, String[] abbreviations, double milliseconds, String pluralName) {
        super(name, abbreviations, milliseconds, pluralName);
    }

    //Smaller than the natural time unit
    public final static TimeUnit NS = new TimeUnit("Nanosecond", "ns", 1e-6, "Nanoseconds");

    //The natural time unit!
    public final static TimeUnit MS = new TimeUnit("Millisecond", "ms", 1, "Milliseconds");

    //Larger than the natural time unit
    public final static TimeUnit S = new TimeUnit("Second", "s", 1000, "Seconds");
    public final static TimeUnit MIN = new TimeUnit("Minute", new String[] {"min", "mn"}, 1000 * 60, "Minutes");
    public final static TimeUnit HR = new TimeUnit("Hour", new String[] {"h", "hr"}, 1000 * 60 * 60, "Hours");
    public final static TimeUnit D = new TimeUnit("Day", "d", 1000 * 60 * 60 * 24, "Days");
    public final static TimeUnit YR = new TimeUnit("Year", new String[] {"y", "yr"}, 1000.0 * 60 * 60 * 24 * 365, "Years");
    public final static TimeUnit JIF = new TimeUnit("Jiffy", new String[] {"jif", "jiff", "jiffy", "jiffies", "jiffie"}, 10, "Jiffies");

    public final static TimeUnit naturalTimeUnit = TimeUnit.MS;

    private static TimeUnit[] unitarrcache;

    //use reflection to find all the static timeunit fields and assemble them into an array
    public static TimeUnit[] getUnits() {
        if(unitarrcache != null) return unitarrcache;

        Field[] fields = TimeUnit.class.getFields();

        ArrayList<TimeUnit> units = new ArrayList<>();
        for(Field f : fields) {
            String name = f.getName();
            boolean nameIsUppercase = name.toUpperCase().equals(name);
            if(nameIsUppercase && f.getType().equals(TimeUnit.class)) {
                try {
                    units.add((TimeUnit) f.get(null));
                } catch(Exception ignored) { }
            }
        }

        unitarrcache = units.toArray(new TimeUnit[0]);
        return unitarrcache;
    }

    public static TimeUnit forAbbreviation(String abbr) {

        for(TimeUnit u : getUnits()) {
            if(u.name.equalsIgnoreCase(abbr) || u.pluralName.equalsIgnoreCase(abbr)) return u;
            for(String a : u.abbreviations) {
                if(a.equals(abbr)) return u;
            }
        }
        return null;
    }

    public static double convertBetween(TimeUnit unitFrom, TimeUnit unitTo, double fromAmount) {
        if(unitFrom == null) throw new IllegalArgumentException("Cannot convert from null");
        if(unitTo == null) throw new IllegalArgumentException("Cannot convert to null");

        return (fromAmount * unitFrom.coefficient) / unitTo.coefficient;
    }

    @Override
    public double convertToNaturalUnit(double u) {
        return convertBetween(this, naturalTimeUnit, u);
    }

    @Override
    public String getCommonAbbrev() {
        if(abbreviations.length > 0) return abbreviations[0];
        else return name;
    }

    @Override
    public Unit getNaturalUnit() {
        return naturalTimeUnit;
    }
}