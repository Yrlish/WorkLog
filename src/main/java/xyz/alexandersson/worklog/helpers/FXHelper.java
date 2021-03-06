/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package xyz.alexandersson.worklog.helpers;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.alexandersson.worklog.WorkLog;

import java.io.IOException;
import java.net.URL;

public class FXHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(FXHelper.class);

    /**
     * Loads an FXML file, filename and location is based of the provided controller class.
     * <p>
     * <b>Example:</b><br>
     * Java: com.example.ApplicationController.class<br>
     * Resource: /com/example/Application.fxml
     * </p>
     *
     * @param controllerClass The controller
     * @param <T>             Class must be extended by {@link Initializable}
     * @return Returns a pair, both the loaded Controller and the root object
     */
    @NotNull
    public static <T extends Initializable> Pair<T, Parent> loadFxml(@NotNull Class<T> controllerClass) {
        URL fxmlFile = controllerClass.getResource(controllerClass.getSimpleName().replace("Controller", ".fxml"));

        if (fxmlFile == null) {
            throw new RuntimeException("Could not find the FXML file for " + controllerClass.getSimpleName());
        } else {
            String url = fxmlFile.toString();
            if (url.contains("!")) {
                url = fxmlFile.toString().split("!")[1];
            }
            LOGGER.debug("Loading {} with {}", url, controllerClass.getName());
        }

        FXMLLoader fxmlLoader = new FXMLLoader(fxmlFile);
        try {
            Parent parent = fxmlLoader.load();
            T controller = fxmlLoader.getController();

            return new Pair<>(controller, parent);
        } catch (IOException e) {
            LOGGER.error("Could not load fxml file {}", fxmlFile.toString().split("!")[1], e);
        }

        return new Pair<>(null, null);
    }

    public static void loadWindow(@NotNull Parent parent, String title, Window owner, boolean resizeable) {
        Stage stage = new Stage();
        Scene scene = new Scene(parent);

        stage.initOwner(owner);
        stage.initModality(Modality.WINDOW_MODAL);

        stage.setTitle(title);
        stage.setScene(scene);
        stage.setResizable(resizeable);

        scene.getStylesheets().add(WorkLog.class.getResource("views/style.css").toExternalForm());

        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                stage.close();
            }
        });

        stage.show();
    }


    public static void showErrorAlert(String title, String header, String text) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initModality(Modality.APPLICATION_MODAL);

        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(text);

        alert.showAndWait();
    }

    public static void addTooltipToTableColumnHeader(TableView table, TableColumn column, Tooltip tooltip) {
        Platform.runLater(() -> {
            Label label = (Label) table.lookup("#" + column.getId() + " .label");
            label.setTooltip(tooltip);
        });
    }
}
