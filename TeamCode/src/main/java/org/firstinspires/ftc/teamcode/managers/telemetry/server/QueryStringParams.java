package org.firstinspires.ftc.teamcode.managers.telemetry.server;

import java.io.BufferedReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;

import static org.firstinspires.ftc.teamcode.managers.telemetry.server.URIEncoding.decode;

public class QueryStringParams {
    private HashMap<String, ArrayList<String>> params;
    public QueryStringParams() {
        this.params = new HashMap<String, ArrayList<String>>();
    }
    public String get(String name) {
        String key = URIEncoding.decode(name);

        if(!params.containsKey(key)) return "";
        if(params.get(key).size() == 0) return "";
        return params.get(key).get(0);
    }
    public String get(String name, int index) {
        String key = URIEncoding.decode(name);

        if(!params.containsKey(key)) return "";
        if(params.get(key).size() <= index) return "";
        return params.get(key).get(index);
    }
    public int getAsInt(String name) {
        try {
            return Integer.parseInt(get(name));
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }
    public int getAsInt(String name, int index) {
        try {
            return Integer.parseInt(get(name, index));
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }
    public boolean has(String name) {
        return params.containsKey(name);
    }
    public void put(String name, String value) {
        String key = URIEncoding.decode(name), val = URIEncoding.decode(value);
        if(!params.containsKey(key)) initParam(key);
        params.get(key).add(val);
    }
    public void initParam(String name) {
        String key = decode(name);
        if(!params.containsKey(key)) params.put(key, new ArrayList<String>());
    }
    public static QueryStringParams from(String qs) {

        String queryNoQuestionmark = qs;
        if(queryNoQuestionmark.startsWith("?")) queryNoQuestionmark = queryNoQuestionmark.substring(1);

        QueryStringParams qsparams = new QueryStringParams();
        String[] params = queryNoQuestionmark.split("&");
        for(String param : params) {
            if(param.equals("")) continue;

            String[] keyVal = param.split("=", 2);
            if(keyVal.length == 1) qsparams.put(keyVal[0], "true");
            else if(keyVal.length == 2) qsparams.put(keyVal[0], keyVal[1]);
        }
        return qsparams;
    }
}
