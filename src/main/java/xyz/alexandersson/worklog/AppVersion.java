package xyz.alexandersson.worklog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class AppVersion {
    private static Logger LOGGER = LoggerFactory.getLogger(AppVersion.class);

    private static boolean loaded = false;

    private static String version = "Unknown";
    private static String buildNumber = "Unofficial";

    private static void load() {
        if (loaded) {
            return;
        }

        try {
            URL url = ((URLClassLoader) AppVersion.class.getClassLoader()).findResource("META-INF/MANIFEST.MF");
            Manifest manifest = new Manifest(url.openStream());

            version = manifest.getMainAttributes().getValue(Attributes.Name.IMPLEMENTATION_VERSION);
            buildNumber = manifest.getMainAttributes().getValue("Build-Number");

            loaded = true;
        } catch (IOException ex) {
            LOGGER.error("Could not read Manifest", ex);
        }
    }

    public static String getVersion() {
        load();
        return version;
    }

    public static String getBuildNumber() {
        load();
        return buildNumber;
    }
}