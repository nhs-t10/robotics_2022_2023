package org.firstinspires.ftc.teamcode.managers.telemetry.server;

import java.io.BufferedReader;
import java.io.IOException;

public class HttpStatusLine {
    public String verb;
    public String path;
    public String[] pathSegments;

    QueryStringParams queryStringParams;

    public HttpStatusLine(String verb, String path) {
        this.verb = verb;
        //remove possible directory escape attacks
        this.path = path.replace("/./", "/").replace("/../", "/");

        this.pathSegments = path.split("/");

        if(path.indexOf('?') != -1) queryStringParams = QueryStringParams.from(path.substring(path.indexOf('?')));
        else queryStringParams = new QueryStringParams();


    }

    public String toString() {
        return this.verb + " " + this.path + " HTTP/1.1";
    }

    public static HttpStatusLine from(BufferedReader reqReader) throws IOException {

        String line = reqReader.readLine();
        String[] words = (line + "").split(" ");

        if(words.length < 2) return new HttpStatusLine("", "");

        return new HttpStatusLine(words[0], words[1]);
    }
}
