package org.firstinspires.ftc.teamcode.managers.telemetry.server;

import org.firstinspires.ftc.teamcode.managers.feature.FeatureManager;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class URIEncoding {
    public static String decode(String str) {
        byte[] chars = new byte[str.length()];
        int indexOffset = 0;

        for(int i = 0; i < str.length(); i++) {
            if(str.charAt(i) == '%') {
                if(i + 1 < str.length() && str.charAt(i + 1) == 'u') {
                    FeatureManager.logger.log("What the heck!?!??!?! The %uxxxx format isn't specified anywhere, it's not standard, it's nEVER supported ANYWHERE. Why do you think you can use it here?!?!?!?!");
                    FeatureManager.logger.log("I'm skipping this character");
                    i += 5;
                    indexOffset -= 6;
                } else {
                    chars[i + indexOffset] = (byte)(Integer.parseInt(str.substring(i + 1, i + 3), 16) & 0xff);
                    i += 2;
                    indexOffset -= 2;
                }
            } else {
                chars[i + indexOffset] = (byte)str.charAt(i);
            }
        }

        byte[] filledChars = Arrays.copyOfRange(chars, 0, chars.length + indexOffset);

        return new String(filledChars, StandardCharsets.UTF_8);
    }

    public static String encode(String str) {

        StringBuilder result = new StringBuilder();
        for(byte c : str.getBytes(StandardCharsets.UTF_8)) {
            if(isSafeCharacter(c)) result.append((char)c);
            else result.append(encodeByte(c));
        }

        return result.toString();
    }

    public static String encodeByte(byte b) {
        int bNum = b & 0xff;
        String hex = Integer.toString(bNum, 16);

        if(hex.length() == 1) hex = "0" + hex;


        hex = hex.toUpperCase();

        return "%" + hex;
    }

    public static boolean isSafeCharacter(int c) {
        char[] unreserved = new char[] {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '_', '.', '~'};
        for(char u : unreserved) {
            if(u == c) return true;
        }
        return false;
    }
}
