package xyz.alexandersson.worklog;

import javafx.application.Application;
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

    private static Logger LOGGER = LoggerFactory.getLogger(WorkLog.class);

    @Override
    public void start(Stage primaryStage) throws Exception {
        Pair<LogController, Parent> fxml = FXHelper.loadFxml(LogController.class);

        Scene scene = new Scene(fxml.getValue());

        primaryStage.setMinHeight(400);
        primaryStage.setMinWidth(600);

        primaryStage.setTitle("WorkLog");
        primaryStage.setScene(scene);
        primaryStage.show();
        LOGGER.info("Application started");
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        DatabaseHelper.closeSessionFactory();
        LOGGER.info("Application stopped");
    }
}
