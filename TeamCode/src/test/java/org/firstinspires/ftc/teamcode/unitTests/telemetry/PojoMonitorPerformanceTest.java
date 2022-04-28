package org.firstinspires.ftc.teamcode.unitTests.telemetry;

import org.firstinspires.ftc.teamcode.managers.telemetry.pojotracker.OhNoJavaFieldMonitorAndExposer;
import org.firstinspires.ftc.teamcode.unitTests.dummy.DummyOpmode;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Test;

import static org.firstinspires.ftc.teamcode.unitTests.matchers.LessThanMatcher.lessThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class PojoMonitorPerformanceTest {
    @Test
    public void test() {
        int TEST_RUNS = 10;

        ComplexPojo complexPojo = new ComplexPojo();
        OhNoJavaFieldMonitorAndExposer monitor = new OhNoJavaFieldMonitorAndExposer(complexPojo, true);
        long timeStart = System.currentTimeMillis();
        for(int i = 0; i < TEST_RUNS; i++) {
            monitor.getJSON();
            complexPojo.scramble();
        }
        long duration = System.currentTimeMillis() - timeStart;
        long durationPerRun = duration / TEST_RUNS;
        assertThat("Average duration per run must be under 1000ms for 1Hz performance", durationPerRun, lessThan(1000));


    }

    public static class ComplexPojo {
        public String testString = "foobar";
        public int testInt = 3;
        public float testFloat = 2.4f;

        private String thisOneShouldntBePrinted;
        public String nullField;

        public Object[] arrayTest = new Object[] {"foo", 3, null};

        public ComplexPojoChild1 objectField = new ComplexPojoChild1();

        public ComplexPojoChild2 tree = new ComplexPojoChild2(0);

        public String getMethodTestString() {
            return "boofar";
        }

        public void scramble() {
            tree.scramble();
        }

        public static class ComplexPojoChild1 {
            public String foobar = "feeeeees!";
            public int[] feefoo = new int[3000];
        }
        public static class ComplexPojoChild2 {
            public ComplexPojoChild2[] childs;
            public String n;
            public ComplexPojoChild2(int depth) {
                n = (Math.random() + "").substring(2);
                if(depth < 3) {
                    this.childs = new ComplexPojoChild2[4];
                    for(int i = 0; i < childs.length; i++) childs[i] = new ComplexPojoChild2(depth + 1);
                } else {
                    this.childs = new ComplexPojoChild2[0];
                }
            }
            public void scramble() {
                if(Math.random() < 0.5) {
                    n = (Math.random() + "").substring(2);
                    for (int i = 0; i < childs.length; i++) childs[i].scramble();
                }
            }
        }
    }


}
