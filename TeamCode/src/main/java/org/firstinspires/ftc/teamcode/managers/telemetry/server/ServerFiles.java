package org.firstinspires.ftc.teamcode.managers.telemetry.server;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;

import androidx.annotation.Nullable;

import org.firstinspires.ftc.teamcode.auxilary.PaulMath;
import org.firstinspires.ftc.teamcode.managers.feature.FeatureManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class ServerFiles {
    /**
     * Get a server asset as a String. If the asset is a non-text file (e.g. an image), this may cause an error.
     * For performance reasons, this should <u>only</u> be used in tests.
     * @param asset A path, relative to the {@code /src/assets/} directory.
     * @return The text of the file, in ASCII, or null if the file doesn't exist.
     * @throws IOException
     */
    @Nullable
    public static String getAssetString(String asset) throws IOException {
        InputStream file = getAssetStream(asset);

        if(file == null) return null;

        StringBuilder r = new StringBuilder();
        while(true) {
            int nextByte = file.read();
            if(nextByte < 0) break;
            r.append((char)nextByte);
        }

        file.close();
        return r.toString();
    }

    /**
     * Get a given asset from the {@code /src/assets} directory, as a stream, or {@code null} if the asset doesn't exist
     * @param asset A path, relative to the {@code /src/assets/} directory.
     * @return An InputStream for the bytes of the file, or {@code null} if the file doesn't exist
     */
    public static InputStream getAssetStream(String asset) {
        String normalizedAssetPath = PaulMath.  normalizeRelativePath(asset);
        try {
            Context context = ((Application) Class.forName("android.app.ActivityThread").getMethod("currentApplication").invoke(null, (Object[]) null)).getApplicationContext();

            AssetManager assets = context.getAssets();

            return assets.open(normalizedAssetPath);
        } catch(Exception e) {
            FeatureManager.logger.log("Unable to get context -- assuming text environment; falling back to FileInputStream loader");
            File file = new File("src/main/assets", normalizedAssetPath);

            try {
                return new FileInputStream(file);
            } catch(FileNotFoundException f) {
                return null;
            }
        }
    }
}
