/*
 *  Copyright (c) 2017 Dennis Alexandersson
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package xyz.alexandersson.worklog;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.alexandersson.worklog.helpers.DatabaseHelper;
import xyz.alexandersson.worklog.helpers.FXHelper;
import xyz.alexandersson.worklog.views.log.LogController;

public class WorkLog extends Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkLog.class);

    private static HostServices hostServices;

    public static HostServices getApplicationHostServices() {
        return hostServices;
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        DatabaseHelper.closeSessionFactory();
        LOGGER.info("Application stopped");
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        hostServices = getHostServices();

        Pair<LogController, Parent> fxml = FXHelper.loadFxml(LogController.class);

        Scene scene = new Scene(fxml.getValue());

        primaryStage.setMinHeight(400);
        primaryStage.setMinWidth(600);

        primaryStage.setTitle(String.format("WorkLog %s (build %s)", AppVersion.getVersion(), AppVersion.getBuildNumber()));
        primaryStage.setScene(scene);
        primaryStage.show();
        LOGGER.info("Application started");
    }
}
