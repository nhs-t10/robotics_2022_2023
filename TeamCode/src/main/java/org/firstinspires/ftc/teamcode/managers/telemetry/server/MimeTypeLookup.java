package org.firstinspires.ftc.teamcode.managers.telemetry.server;

public abstract class MimeTypeLookup {

    private static String[][] map = new String[][] {
        {"png", "image/png"},
        {"js", "application/javascript"}
    };

    public static String lookupFileExtension(String fileExtension) {
        for(String[] e : map) {
            if(e[0].equals(fileExtension)) return e[1];
        }
        return null;
    }
}
