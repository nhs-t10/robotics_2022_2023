package org.firstinspires.ftc.teamcode.unitTests.matchers;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

public class LessThanMatcher extends BaseMatcher {
    private double lt;
    public LessThanMatcher(double lt) {
        this.lt = lt;
    }
    @Override
    public boolean matches(Object item) {
        if(!(item instanceof Number)) return false;
        long longVal = ((Number)item).longValue();
        if(longVal > Double.MAX_VALUE || longVal < Double.MIN_VALUE) return longVal < lt;

        return ((Number) item).doubleValue() < lt;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("Less than " + lt);
    }

    public static LessThanMatcher lessThan(double lt) {
        return new LessThanMatcher(lt);
    }
}
