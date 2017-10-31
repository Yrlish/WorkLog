/*
 * MIT License
 *
 * Copyright © 2017 Dennis Alexandersson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
