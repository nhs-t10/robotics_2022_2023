package org.firstinspires.ftc.teamcode.auxilary;

import static java.lang.Math.PI;

import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.managers.feature.FeatureManager;
import org.firstinspires.ftc.teamcode.managers.feature.robotconfiguration.RobotConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;

public abstract class PaulMath extends FeatureManager {

    public static float highestMagnitude(float[] unknown) {
        float highest = unknown[0];
        for (int i = 1; i < unknown.length; i++) {
            highest = Math.max(highest,Math.abs(unknown[i]));
        }
        return highest;
    }

    public static float[] rgbToHsl(float r, float g, float b) {
        r /= 255; g /= 255; b /= 255;

        float max = Math.max(r, Math.max(g, b));
        float min = Math.min(r, Math.min(g, b));
        float h, s, l = (max + min) / 2;

        if (max == min) {
            h = s = 0; // achromatic
        } else {
            float d = max - min;
            s = l > 0.5 ? d / (2 - max - min) : d / (max + min);

            if(max == r) h = (g - b) / d + (g < b ? 6 : 0);
            else if(max == g) h = (b - r) / d + 2;
            else h = (r - g) / d + 4;

            h /= 6;
        }

        return new float[] { h, s, l };
    }

    public static float[] normalizeArray(float[] arrray) { //yes the 3 r's is intentional
        float highest = highestMagnitude(arrray);
        for (int i = 0; i < arrray.length; i++) {
            arrray[i] = arrray[i] / highest;
        }
        return arrray;
    }

    public static float[] cartesianToPolar(float x, float y) {
        double radius = Math.sqrt((x*x) + (y*y));
        double angle = Math.atan2(y, x);

        return new float[] {(float)radius, (float)(angle * 180 / PI)};
    }

    public static float[] polarToCartesian(float angle, float magnitude) {

        double y = Math.sin(angle) * magnitude;
        double x = Math.cos(angle) * magnitude;

        return new float[] {(float)x, (float)y };
    }

    /**
     * Calculates motor powers
     * @param verticalPower desired vertical movement, from -1 to 1
     * @param horizontalPower desired horizontal movement, from -1 to 1
     * @param rotationalPower desired rotational movement, from -1 to 1
     * @return motor powers, in the order fl, fr, br, bl.
     */
    public static float[] omniCalc(float verticalPower, float horizontalPower, float rotationalPower) {
        float v = Range.clip(verticalPower, -1, 1);
        float h = Range.clip(horizontalPower, -1, 1);
        float r = Range.clip(rotationalPower, -1, 1);

        RobotConfiguration configuration = FeatureManager.getRobotConfiguration();

        // Motor powers of fl, fr, br, bl
        float[] vertical = {configuration.omniComponents.ver.fl * v,
                            configuration.omniComponents.ver.fr * v,
                            configuration.omniComponents.ver.br * v,
                            configuration.omniComponents.ver.bl * v};

        float[] horizontal = {configuration.omniComponents.hor.fl * h,
                            configuration.omniComponents.hor.fr * h,
                            configuration.omniComponents.hor.br * h,
                            configuration.omniComponents.hor.bl * h};

        float[] rotational = {configuration.omniComponents.rot.fl * r,
                            configuration.omniComponents.rot.fr * r,
                            configuration.omniComponents.rot.br * r,
                            configuration.omniComponents.rot.bl * r};

        float[] sum = new float[4];
        for (int i = 0; i < 4; i++) {
            sum[i] = vertical[i] + horizontal[i] + rotational[i];
        }
        //This makes sure that no value is greater than 1 by dividing all of them by the maximum
        float highest = highestMagnitude(sum);
        if (highest > 1) {
            sum = normalizeArray(sum);
            return sum;
        }
        return sum;
    }
    public static float[] omniCalcInverse(float _fl, float _fr, float _br, float _bl) {
        return omniCalcInverse(_fl, _fr, _br, _bl, false);
    }

    /**
     *
     * @param _fl
     * @param _fr
     * @param _br
     * @param _bl
     * @param measurementsFromRawMotor
     * @return the omni coefficients, in VHR order.
     */
    public static float[] omniCalcInverse(float _fl, float _fr, float _br, float _bl, boolean measurementsFromRawMotor) {
        RobotConfiguration configuration = FeatureManager.getRobotConfiguration();

        //undo motor-level coefficients, if requested
        //ugh i don't like android studio's "ooh don't modify parameters" rule >:(
        float fl = measurementsFromRawMotor ? _fl / configuration.motorCoefficients.fl : _fl;
        float fr = measurementsFromRawMotor ? _fr / configuration.motorCoefficients.fr : _fr;
        float bl = measurementsFromRawMotor ? _bl / configuration.motorCoefficients.bl : _bl;
        float br = measurementsFromRawMotor ? _br / configuration.motorCoefficients.br : _br;

        //for each motor, divide its coefficients (expected full power) with the given motor powers (actual power).
        float[] flPercentages = {
                fl / configuration.omniComponents.ver.fl,
                fl / configuration.omniComponents.hor.fl,
                fl / configuration.omniComponents.rot.fl
        };
        float[] frPercentages = {
                fr / configuration.omniComponents.ver.fr,
                fr / configuration.omniComponents.hor.fr,
                fr / configuration.omniComponents.rot.fr
        };
        float[] brPercentages = {
                br / configuration.omniComponents.ver.br,
                br / configuration.omniComponents.hor.br,
                br / configuration.omniComponents.rot.br
        };
        float[] blPercentages = {
                bl / configuration.omniComponents.ver.bl,
                bl / configuration.omniComponents.hor.bl,
                bl / configuration.omniComponents.rot.bl
        };

        float[] sum = {
                blPercentages[0] + brPercentages[0] + frPercentages[0] + flPercentages[0],
                blPercentages[1] + brPercentages[1] + frPercentages[1] + flPercentages[1],
                blPercentages[2] + brPercentages[2] + frPercentages[2] + flPercentages[2],
        };

        return sum;
    }

    public static float roundToPoint(float input, float place) {
        return Math.round(input / place) * place;
    }

    /**
     * Converts a number of ticks to centimeters
     * @param ticks Number of ticks
     * @return Centimeters covered by the robot in the given number of ticks
     */
    public static float encoderDistanceCm(double ticks) {
        RobotConfiguration config = FeatureManager.getRobotConfiguration();
        double rotations = ticks / config.encoderTicksPerRotation;
        return (float) ((config.wheelCircumference * rotations * config.gearRatio) / config.slip);
    }

    public static float delta(float one, float two) {
        return Math.abs(one - two);
    }

    public static String camelToSnake(String camel) {
        ArrayList<String> words = new ArrayList<String>();

        String currentWord = "";
        for (char letter : camel.toCharArray()) {

            if (Character.isUpperCase(letter)) {
                //if multiple uppercase in a row, don't break words
                if (!currentWord.toUpperCase().equals(currentWord)) {
                    words.add(currentWord);
                    currentWord = "";
                }
            }

            currentWord += letter;


        }
        //add the final word if it's relevant
        if (!currentWord.equals("")) words.add(currentWord);

        //join into snake
        StringBuilder snakey = new StringBuilder();
        for (int i = 0; i < words.size(); i++) {
            snakey.append(words.get(i));
            //if it's not the last word, add the underscore
            if (i + 1 < words.size()) snakey.append("_");
        }

        return snakey.toString().toUpperCase();
    }

    /**
     * Convert a kebab-case string to PascalCase.
     * @param str a string in kebab-case, like "word-a-b-c-d"
     * @return the same string in PascalCaseAlsoKnownAsUpperCamelCase
     */
    public static String kebabToPascal(String str) {
        String[] words = str.split("-");
        StringBuilder s = new StringBuilder();
        for(String w : words) {
            s.append(capitalize(w));
        }
        return s.toString();
    }

    /**
     * Make a word into capital-case.
     * @param word a string with no whitespace
     * @return the given word, but with the first letter capitalized.
     */
    public static String capitalize(String word) {
        return word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
    }

    /**
     * Counts proportional error for PID control.
     *
     * @param currentValue  the value we are currently at
     * @param expectedValue the value we want
     * @return amount to change in order to get to the target value quickly and smoothly
     */
    public static float proportionalPID(float currentValue, float expectedValue) {
        return proportionalPID(currentValue, expectedValue, 0.007f);
    }

    public static float proportionalPID(float currentValue, float expectedValue, float Kp) {
        return proportionalPID(currentValue, expectedValue, Kp, 0.2f, 0.5f);
    }

    /**
     * Counts proportional error for PID control.
     *
     * @param currentValue  the value we are currently at
     * @param expectedValue the value we want
     * @param Kp proportional coefficient. 0.007 by default
     * @param clipMin Minimum absolute value that the output can be. 0.2 by default.
     * @param clipMax Maximum absolute value that the output can be. 0.5 by default.
     * @return a constant times the difference between the paramaters
     */
    public static float proportionalPID(float currentValue, float expectedValue, float Kp, float clipMin, float clipMax) {
        float currentBased360 = (currentValue + 360) % 360;
        float targetBased360 = (expectedValue + 360) % 360;

        float difference = (targetBased360 - currentBased360);

        if(difference > 180) difference += -360;
        else if(difference < -180) difference += 360;

        if (Math.abs(difference) > 1) {
            //abs val then multiply by sign of difference; easier than two ifs for positive clip and negative clip
            return (Range.clip((Kp * Math.abs(difference)), clipMin, clipMax)) * (difference<=0?-1:1);
        } else {
            return 0;
        }
    }

    /**
     * Counts proportional error for PID control.
     *
     * @param currentValue  the value we are currently at
     * @param expectedValue the value we want
     * @param Kp proportional coefficient. 0.007 by default
     * @return a constant times the difference between the paramaters
     */
    public static float generalProportionalPID(float currentValue, float expectedValue, float Kp) {
        float difference = (expectedValue - currentValue);

        //abs val then multiply by sign of difference; easier than two ifs for positive clip and negative clip
        return Kp * difference;
    }

    /**
     * Escape a string, so as to put it in a JSON object or Java source string. The inverse of unescapeString()
     * @param value The string to escape
     * @return Escaped string
     */
    public static String escapeString(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\"", "\\\"")
                .replace("\t", "\\t");
    }

    /**
     * Unescape a string like it was in a JSON object. The inverse of escapeString().
     * @param value The escaped string
     * @return Unescaped string
     */
    public static String unescapeString(String value) {
        StringBuilder result = new StringBuilder();
        char[] chars = value.toCharArray();

        boolean escaped = false;
        for (char c : chars) {
            if (escaped) {
                escaped = false;
                if (c == 'n') result.append('\n');
                else if (c == 'r') result.append('\r');
                else if (c == 't') result.append('\t');
                else result.append(c);
            } else if (c == '\\') {
                escaped = true;
            } else {
                result.append(c);
            }
        }
        return result.toString();

    }

    public static String join(String s, String[] a) {
        StringBuilder r = new StringBuilder();
        for(int i = 0; i < a.length; i++) {
            if(i!=0) r.append(s);
            r.append(a[i]);
        }
        return r.toString();
    }

    public static String deJSONify(String s) {
        if(s.length() > 2 && s.startsWith("\"") && s.endsWith("\"")) {
            return unescapeString(s.substring(1, s.length() - 1));
        } else {
            return s;
        }
    }

    public static String JSONify(String s) {
        return '"' + escapeString(s) + '"';
    }
    public static String JSONify(Object s) {
        if(s == null) return "null";
        else if(s instanceof Number || s instanceof Boolean) return s.toString();
        else if(s.getClass().isArray()) return JSONifyArray(s);
        else return JSONify(s.toString());
    }
    private static String JSONifyArray(Object s) {
        if(s instanceof double[]) return Arrays.toString((double[])s);
        if(s instanceof boolean[]) return Arrays.toString((boolean[])s);
        if(s instanceof int[]) return Arrays.toString((int[])s);
        if(s instanceof float[]) return Arrays.toString((float[])s);
        if(s instanceof long[]) return Arrays.toString((long[])s);
        if(s instanceof char[]) return Arrays.toString((char[])s);

        Object[] ar = (Object[])s;
        StringBuilder str = new StringBuilder();
        for(Object o : ar) {
            str.append(",").append(JSONify(o));
        }

        return "[" + str.toString().substring(1) + "]";
    }

    public static boolean isJSONable(Object s) {
        return s == null || s instanceof Number || s instanceof Boolean || s instanceof String || s instanceof Character;
    }

    public static double rotsToTicks(double rotations){
        return rotations*28;
    }
    public static double metersToRots(double meters){
        return meters*0.1*PI;
    }
    public static double metersToTicks(double meters){
        return meters*2.8*PI;
    }

    public static String[] concatArrays(String[]... arrs) {
        //get the total length of the new array
        int m = 0;
        for(String[] a : arrs) m += a.length;

        //make a result array
        String[] r = new String[m];

        int c = 0;
        for(String[] a : arrs) {
            for (String s : a) {
                r[c] = s;
                c++;
            }
        }

        return r;
    }

    /**
     * Takes all input from {@code in} and puts it into {@code out}. Closes {@code in} after it's done. Will block and not return until the piping is complete.
     * @param in the stream to get data from. Will be closed.
     * @param out where to stream the to
     * @throws IOException if something happens with the streaming.
     */
    public static void pipeStream(InputStream in, OutputStream out) throws IOException {
        //if either's null, don't care.
        if(in == null || out == null) return;

        try {
            byte[] buffer = new byte[1024];
            int len;
            while(true) {
                len = in.read(buffer, 0, Math.max(1, Math.min(buffer.length - 1, in.available())));
                if(len == -1) break;
                out.write(buffer, 0, len);
            }
        } finally {
            in.close();
        }
    }

    /**
     * Normalizes a path to POSIX-esque-ness. Removes repeating slashes and leading slashes; resolves ".." and "." terms;
     * @param path The path to normalize, in some format
     * @return a normalised path in POSIX format.
     */
    public static String normalizeRelativePath(String path) {
        if(path == null) return "";
        return path
                .replace('\\', '/')
                .replaceAll("/+", "/")
                .replaceAll("(^|/)\\./", "/")
                .replaceAll("(^|[^/]+)/\\.\\./", "")
                .replaceAll("^/", "");
    }

    /**
     * Trims from the start of {@code toTrim} until its start DOESN'T match {@code trimBy}
     * @param toTrim
     * @param trimBy
     * @return the trimmed string
     */
    public static String trimMatchingStart(String toTrim, String trimBy) {
        if(trimBy == null) return toTrim;
        if(toTrim == null) return "";

        char[] toTrimArr = toTrim.toCharArray(), trimByArr = trimBy.toCharArray();
        int trimIndex = 0;
        int len = Math.min(toTrim.length(), trimBy.length());
        for(int i = 0; i < len; i++) {
            if(toTrimArr[i] == trimByArr[i]) trimIndex = i;
        }
        return toTrim.substring(trimIndex);
    }

    public static String leftPad(int l, String n) {
        int initialLength = n.length();
        StringBuilder s = new StringBuilder(n);
        for(int i = initialLength; i < l; i++) s.insert(0, " ");
        return s.toString();
    }

    /**
     * Repeat the string s a given time.
     * @param s the string to repeat
     * @param t the number of times to repeat it
     * @return the repeated string
     */
    public static String repeat(String s, int t) {
        if(t <= 0) return "";

        StringBuilder r = new StringBuilder();
        for(int i = t; i > 0; i--) r.append(s);
        return r.toString();
    }

    /**
     * Remove the package from a fully-qualified class
     * @param fullyQualifiedClass a fully-qualified (with package) Java class name
     * @return the unqualified (without package) Java class name
     */
    public static String classBasename(String fullyQualifiedClass) {
        if(fullyQualifiedClass == null) return null;

        int lengthCached = fullyQualifiedClass.length();
        int index = 0;
        for(int i = 0; i < lengthCached; i++) {
            if(Character.isUpperCase(fullyQualifiedClass.charAt(i))) {
                index = i;
                break;
            }
        }
        return fullyQualifiedClass.substring(index);
    }

    /**
     * Return the "basename" of a file-- essentially, remove the folder.
     * @param fileName a directory path, in either linux or windows format.
     * @return the last term in the path
     */
    public static String basename(String fileName) {
        int idx = Math.max(
                fileName.lastIndexOf('/'),
                fileName.lastIndexOf('\\') //support both windows and linux
        );
        return fileName.substring(idx + 1);
    }

    public static double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }
}
