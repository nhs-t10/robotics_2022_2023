package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model;

import org.firstinspires.ftc.teamcode.auxilary.PaulMath;

public class Location {

    public static String defaultFileName = "";

    public String fileName;
    public String statepath;
    public int stateNumber;


    public int line;
    public int col;

    public Location clone() {
        return new Location(fileName, statepath, stateNumber, line, col);
    }

    //static alias to constructor
    //for code compression purposes
    public static void L(AutoautoProgramElement e, String statepath, int stateNumber, int line, int col) {
        e.setLocation(new Location(statepath, stateNumber, line, col));
    }

    public Location(String statepath, int stateNumber, int line, int col) {
        this(defaultFileName, statepath, stateNumber, line, col);
    }

    public Location(String fileName, String statepath, int stateNumber, int line, int col) {
        assert statepath != null;

        this.fileName = fileName;
        this.statepath = statepath;
        this.stateNumber = stateNumber;
        this.line = line;
        this.col = col;
    }

    public String toString() {
        return statepath + "[" + stateNumber + "] (" + PaulMath.basename(fileName) + ":" + line + ":" + col + ")";
    }

    public void setStatepath(String name) {
        this.statepath = name;
    }
}
