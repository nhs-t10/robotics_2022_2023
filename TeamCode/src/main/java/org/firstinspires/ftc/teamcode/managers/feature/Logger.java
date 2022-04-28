package org.firstinspires.ftc.teamcode.managers.feature;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.auxilary.PaulMath;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.Location;

import java.io.PrintStream;
import java.util.Arrays;

public class Logger {
    private static int CONSOLE_WIDTH = 180;

    public Telemetry.Log backend;
    public PrintStream fallbackBackend;
    private boolean usesFallback;

    private boolean addStackTrace;

    private boolean recordLogHistory;
    private String logHistory = "";

    public String getLogHistory() {
        return logHistory;
    }
    public void setRecordLogHistory(boolean recordLogHistory) {
        this.recordLogHistory = recordLogHistory;
        //erase old history; we don't need it
        logHistory = "";
    }

    public void setBackend(Telemetry.Log log) {
        backend = log;
        usesFallback = false;
    }

    public void setBackend(PrintStream log) {
        fallbackBackend = log;
        usesFallback = true;
    }

    public Logger() {
        this.fallbackBackend = System.out;
        this.usesFallback = true;
    }

    public Logger(Telemetry.Log log) { this.backend = log;}

    public void log(String a) {
        log(a, null);
    }
    //allow the user to specify a location so autoauto can get first-class logging
    public void log(String a, Location location) {
        if(recordLogHistory) logHistory += a + "\n";
        //make it into a local variable so Android Studio doesn't complain
        String l = a;
        if(addStackTrace) {
            if(location == null) l += PaulMath.leftPad(CONSOLE_WIDTH - l.length(), getLastNonLoggerStackElement());
            else l += PaulMath.leftPad(CONSOLE_WIDTH - l.length(), location.toString());
        }
        if(l == null) {
            log("null");
        } else {
            if (usesFallback) fallbackBackend.println(l);
            else backend.add(l);
        }
    }

    private String getLastNonLoggerStackElement() {
        String selfClassName = getClass().getCanonicalName();
        StackTraceElement[] stackTrace = (new Error()).getStackTrace();
        for(StackTraceElement e : stackTrace) {
            String element = e.toString();
            if(!element.startsWith(selfClassName)) {
                return PaulMath.classBasename(element);
            }
        }
        return stackTrace[stackTrace.length - 1].toString();
    }

    public void log(String n, Object l) {
        if(!usesFallback) backend.add(n, l);
    }

    public void log(Object l) {log(l + "");}
    public void log(double l) {log("" + l);}
    public void log(boolean l) {log("" + l);}
    public void log(int l) {log("" + l);}
    public void log(float l) {log("" + l);}
    public void log(long l) {log("" + l);}
    public void log(char l) {log("" + l);}
    public void log(Object[] l) {log(Arrays.toString(l));}
    public void log(double[] l) { log(Arrays.toString(l));}
    public void log(boolean[] l) { log(Arrays.toString(l));}
    public void log(int[] l) { log(Arrays.toString(l));}
    public void log(float[] l) { log(Arrays.toString(l));}
    public void log(long[] l) { log(Arrays.toString(l));}
    public void log(char[] l) { log(Arrays.toString(l));}

    public void add(Object l) { log(l); }
    public void add(double l) { log(l); }
    public void add(boolean l) { log(l); }
    public void add(int l) { log(l); }
    public void add(float l) { log(l); }
    public void add(long l) { log(l); }
    public void add(char l) { log(l); }
    public void add(Object[] l) { log(l); }
    public void add(double[] l) { log(l); }
    public void add(boolean[] l) { log(l); }
    public void add(int[] l) { log(l); }
    public void add(float[] l) { log(l); }
    public void add(long[] l) { log(l); }
    public void add(char[] l) { log(l); }

    public void println(Object l) { log(l); }
    public void println(double l) { log(l); }
    public void println(boolean l) { log(l); }
    public void println(int l) { log(l); }
    public void println(float l) { log(l); }
    public void println(long l) { log(l); }
    public void println(char l) { log(l); }
    public void println(Object[] l) { log(l); }
    public void println(double[] l) { log(l); }
    public void println(boolean[] l) { log(l); }
    public void println(int[] l) { log(l); }
    public void println(float[] l) { log(l); }
    public void println(long[] l) { log(l); }
    public void println(char[] l) { log(l); }

    public void warn(Object l) {log("WARNING: " + l);}
    public void warn(double l) {log("WARNING: " + l);}
    public void warn(boolean l) {log("WARNING: " + l);}
    public void warn(int l) {log("WARNING: " + l);}
    public void warn(float l) {log("WARNING: " + l);}
    public void warn(long l) {log("WARNING: " + l);}
    public void warn(char l) {log("WARNING: " + l);}
    public void warn(Object[] l) {log("WARNING: " + Arrays.toString(l));}
    public void warn(double[] l) {log("WARNING: " + Arrays.toString(l));}
    public void warn(boolean[] l) {log("WARNING: " + Arrays.toString(l));}
    public void warn(int[] l) {log("WARNING: " + Arrays.toString(l));}
    public void warn(float[] l) {log("WARNING: " + Arrays.toString(l));}
    public void warn(long[] l) {log("WARNING: " + Arrays.toString(l));}
    public void warn(char[] l) {log("WARNING: " + Arrays.toString(l));}


    public void println(String l) {
        log(l);
    }
    public void add(String l) {
        log(l);
    }

    public void logToPhone(String l) {
        if(usesFallback) fallbackBackend.println(l);
        else backend.add(l);
    }
}