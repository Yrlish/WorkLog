/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package xyz.alexandersson.worklog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class AppVersion {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppVersion.class);

    private static String appName = "Unnamed";
    private static String version = "Unknown";
    private static String buildNumber = "Unofficial";
    private static ZonedDateTime buildTime;

    static  {
        try {
            URL url = ((URLClassLoader) AppVersion.class.getClassLoader()).findResource("META-INF/MANIFEST.MF");
            Manifest manifest = new Manifest(url.openStream());

            appName = manifest.getMainAttributes().getValue(Attributes.Name.IMPLEMENTATION_TITLE);
            version = manifest.getMainAttributes().getValue(Attributes.Name.IMPLEMENTATION_VERSION);
            buildNumber = manifest.getMainAttributes().getValue("Build-Number");
            buildTime = ZonedDateTime.parse(manifest.getMainAttributes().getValue("Build-Time"), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ"));
        } catch (IOException ex) {
            LOGGER.error("Could not read Manifest", ex);
        }
    }

    public static String getAppName() {
        return appName;
    }

    public static String getVersion() {
        return version;
    }

    public static String getBuildNumber() {
        return buildNumber;
    }

    public static ZonedDateTime getBuildTime() {
        return buildTime;
    }
}
