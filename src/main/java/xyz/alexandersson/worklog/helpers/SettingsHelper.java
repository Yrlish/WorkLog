/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package xyz.alexandersson.worklog.helpers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;

import static java.util.Arrays.asList;

public class SettingsHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsHelper.class);

    public static final Path CONFIG_FOLDER;
    private static final Path CONFIG_FILE;
    private static final Properties PROPERTIES = new Properties();

    static {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            CONFIG_FOLDER = Paths.get(System.getProperty("user.home"), "AppData", "Roaming", "WorkLog");
            CONFIG_FOLDER.toFile().mkdirs();
            CONFIG_FILE = Paths.get(CONFIG_FOLDER.toString(), "config.xml");
            LOGGER.debug("Detected Windows, using '{}'...", CONFIG_FILE);
        } else if (os.contains("mac")) {
            CONFIG_FOLDER = Paths.get(System.getProperty("user.home"), "Library", "Application Support", "WorkLog");
            CONFIG_FOLDER.toFile().mkdirs();
            CONFIG_FILE = Paths.get(CONFIG_FOLDER.toString(), "config.xml");
            LOGGER.debug("Detected Mac, using '{}'...", CONFIG_FILE);
        } else if (os.contains("nix") || os.contains("nux") || os.indexOf("aix") > 0) {
            CONFIG_FOLDER = Paths.get(System.getProperty("user.home"), ".WorkLog");
            CONFIG_FOLDER.toFile().mkdirs();
            CONFIG_FILE = Paths.get(CONFIG_FOLDER.toString(), "config.xml");
            LOGGER.debug("Detected Linux, using '{}'...", CONFIG_FILE);
        } else {
            CONFIG_FOLDER = Paths.get(".");
            CONFIG_FILE = Paths.get("config.xml");
            LOGGER.info("Could not detect a predefined OS, using '{}'...", CONFIG_FILE);
        }
    }

    public static void load() {
        LOGGER.debug("Loading configuration {}", CONFIG_FILE);

        if (CONFIG_FILE.toFile().exists()) {
            try (InputStream is = new FileInputStream(CONFIG_FILE.toFile())) {
                PROPERTIES.loadFromXML(is);
            } catch (IOException e) {
                LOGGER.error("Could not load config file.", e);
            }
        } else {
            LOGGER.info("No config found, using default values...");
        }

        // Always set defaults, but only to properties that doesn't exist yet.
        // E.g. new config or when updating the software with a new config.
        if (setDefaults()) {
            save();
        }
    }

    private static boolean setDefaults() {
        boolean updated = false;

        if (!PROPERTIES.containsKey("maxItemsInLists")) {
            PROPERTIES.setProperty("maxItemsInLists", "-1");
            updated = true;
        }

        return updated;
    }

    public static void setProperty(String key, String value) {
        if (!PROPERTIES.containsKey(key) || !PROPERTIES.getProperty(key).equals(value)) {
            PROPERTIES.setProperty(key, value);
            save();
        }
    }

    public static Optional<String> getProperty(String key) {
        return Optional.ofNullable(PROPERTIES.getProperty(key));
    }

    public static Optional<Boolean> getBooleanProperty(String key) {
        String prop = PROPERTIES.getProperty(key).toLowerCase();
        Boolean retval = null;

        if (asList("true", "yes", "t", "y").contains(prop)) {
            retval = true;
        } else if (asList("false", "no", "f", "n").contains(prop)) {
            retval = false;
        }

        return Optional.ofNullable(retval);
    }

    private static void save() {
        try (OutputStream os = new FileOutputStream(CONFIG_FILE.toFile())) {
            PROPERTIES.storeToXML(os, null, "UTF-8");
        } catch (IOException e) {
            LOGGER.error("Could not load config file.", e);
        }
    }
}
