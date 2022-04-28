package org.firstinspires.ftc.teamcode.auxilary;


import android.app.Application;
import android.content.Context;

import org.firstinspires.ftc.teamcode.managers.feature.FeatureManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;

public class FileSaver {
    private String filePathname;
    private Context context;
    public String fileName;

    public FileSaver(String _fileName) {
        try {
            Context context = ((Application) Class.forName("android.app.ActivityThread").getMethod("currentApplication").invoke(null, (Object[]) null)).getApplicationContext();
            this.fileName = _fileName;
            this.filePathname = context.getExternalFilesDir(null).getPath() + "/" + fileName;
        } catch (Throwable ignored) {
            this.fileName = null;
            this.filePathname = null;
        }
    }

    public ArrayList<String> readLines() {

        String thisLine;
        BufferedReader textFile;
        ArrayList<String> lines = new ArrayList<String>();

        if(this.filePathname == null) return lines;

        try (BufferedReader br = new BufferedReader(new BufferedReader(new InputStreamReader(new FileInputStream(filePathname))))) {

            while ((thisLine = br.readLine()) != null) {
                lines.add(thisLine);
            }
        } catch (Throwable e) {
            FeatureManager.logger.log(e.toString());
            for(StackTraceElement t : e.getStackTrace()) FeatureManager.logger.log(t);
        }
        return lines;
    }

    public String readContent() {

        if(this.filePathname == null) return "";

        StringBuilder result = new StringBuilder();

        try(BufferedReader textFile = new BufferedReader(new InputStreamReader(new FileInputStream(filePathname)))) {
            int nextChar;
            while ((nextChar = textFile.read()) != -1) {
                result.append((char)nextChar);
            }
        } catch (Throwable e) {
            FeatureManager.logger.log(e.toString());
            for(StackTraceElement t : e.getStackTrace()) FeatureManager.logger.log(t);
        }
        return result.toString();
    }

    public void deleteFile() {
        if(this.filePathname == null) return;

        File file = new File(filePathname);
        file.delete();
    }

    public void overwriteFile(String newContent) {
        if(this.filePathname == null) return;

        try (BufferedWriter output = new BufferedWriter(new FileWriter(filePathname))) {
            output.write(newContent);
        } catch (Throwable e) {
            FeatureManager.logger.log(e.toString());
            for(StackTraceElement t : e.getStackTrace()) FeatureManager.logger.log(t);
        }
    }

    public void appendLine(String line) {
        if(this.filePathname == null) return;

        try (BufferedWriter output = new BufferedWriter(new FileWriter(filePathname, true))) {
            output.newLine();
            output.write(line);
        } catch (Throwable e) {
            FeatureManager.logger.log(e.toString());
            for(StackTraceElement t : e.getStackTrace()) FeatureManager.logger.log(t);
        }
    }

}
