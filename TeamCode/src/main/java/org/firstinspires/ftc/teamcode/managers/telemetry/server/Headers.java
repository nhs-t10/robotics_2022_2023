package org.firstinspires.ftc.teamcode.managers.telemetry.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Headers {
    private HashMap<String, ArrayList<String>> headers;
    public Headers() {
        this.headers = new HashMap<String, ArrayList<String>>();
    }
    public String toString() {
        return headers.toString();
    }
    public String get(String name) {
        String val = tryGet(name);
        if(val == null) return "";
        else return val;
    }
    public String tryGet(String name) {
        String key = name.trim().toLowerCase();
        if(!headers.containsKey(key)) return null;
        if(headers.get(key).size() == 0) return null;
        return headers.get(key).get(0);
    }
    public int getAsInt(String name) {
        try {
            return Integer.parseInt(get(name));
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }
    public boolean hasHeader(String name) {
        return tryGet(name) != null;
    }
    public void add(String name, String value) {
        String key = name.trim().toLowerCase(), val = value.trim();
        if(!headers.containsKey(key)) initHeader(key);
        headers.get(key).add(val);
    }
    public void initHeader(String name) {
        String key = name.trim().toLowerCase();
        if(!headers.containsKey(key)) headers.put(key, new ArrayList<String>());
    }
    public static Headers from(BufferedReader reader) throws IOException {
        Headers headers = new Headers();
        while(true) {
            String line = reader.readLine();
            if(line == null) break;

            line = line.trim();
            if(line.equals("")) break;

            String[] keyVal = line.split(":", 2);

            //if the line isn't of the form header:value, discard it
            if(keyVal.length != 2) continue;

            headers.add(keyVal[0], keyVal[1]);
        }
        return headers;
    }
}
