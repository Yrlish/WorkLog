/*
 * Copyright 2017 Dennis Alexandersson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
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

    private static boolean loaded = false;

    private static String appName = "Unnamed";
    private static String version = "Unknown";
    private static String buildNumber = "Unofficial";
    private static ZonedDateTime buildTime;

    private static void load() {
        if (loaded) {
            return;
        }

        try {
            URL url = ((URLClassLoader) AppVersion.class.getClassLoader()).findResource("META-INF/MANIFEST.MF");
            Manifest manifest = new Manifest(url.openStream());

            appName = manifest.getMainAttributes().getValue(Attributes.Name.IMPLEMENTATION_TITLE);
            version = manifest.getMainAttributes().getValue(Attributes.Name.IMPLEMENTATION_VERSION);
            buildNumber = manifest.getMainAttributes().getValue("Build-Number");
            buildTime = ZonedDateTime.parse(manifest.getMainAttributes().getValue("Build-Time"), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ"));

            loaded = true;
        } catch (IOException ex) {
            LOGGER.error("Could not read Manifest", ex);
        }
    }

    public static String getAppName() {
        return appName;
    }

    public static String getVersion() {
        load();
        return version;
    }

    public static String getBuildNumber() {
        load();
        return buildNumber;
    }

    public static ZonedDateTime getBuildTime() {
        load();
        return buildTime;
    }
}
