/*
 *  Copyright (c) 2017 Dennis Alexandersson
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package xyz.alexandersson.worklog.helpers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class SettingsHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsHelper.class);

    private static final Path CONFIG_FILE;
    private static final Properties PROPERTIES = new Properties();

    static {
        String os = System.getProperty("os.name").toLowerCase();
        File configFolder = Paths.get(System.getProperty("user.home"), ".WorkLog").toFile();

        if (os.contains("win")) {
            configFolder.mkdir();
            try {
                Files.setAttribute(configFolder.toPath(), "dos:hidden", true);
            } catch (IOException e) {
                LOGGER.error("Could not make config folder hidden.", e);
            }

            CONFIG_FILE = Paths.get(configFolder.toString(), "config.xml");
            LOGGER.debug("Detected Windows, using '{}'...", CONFIG_FILE);
        } else if (os.contains("mac")) {
            configFolder = Paths.get(System.getProperty("user.home"), "Library", "Application Support", "WorkLog").toFile();
            configFolder.mkdirs();
            CONFIG_FILE = Paths.get(configFolder.toString(), "config.xml");
            LOGGER.debug("Detected Windows, using '{}'...");
        } else if (os.contains("nix") || os.contains("nux") || os.indexOf("aix") > 0) {
            configFolder.mkdir();
            CONFIG_FILE = Paths.get(configFolder.toString(), "config.xml");
            LOGGER.debug("Detected Windows, using '{}'...", CONFIG_FILE);
        } else {
            CONFIG_FILE = Paths.get("config.xml");
            LOGGER.debug("Could not detect a predefined OS, using '{}'...");
        }
    }

    public static void load() {
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
        setDefaults();
    }

    private static void setDefaults() {
        if (!PROPERTIES.containsKey("maxItemsInLists")) {
            PROPERTIES.setProperty("maxItemsInLists", "100");
        }
    }

    public static void save() {
        try (OutputStream os = new FileOutputStream(CONFIG_FILE.toFile())) {
            PROPERTIES.storeToXML(os, null, "UTF-8");
        } catch (IOException e) {
            LOGGER.error("Could not load config file.", e);
        }
    }
}
