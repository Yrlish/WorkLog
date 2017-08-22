package xyz.alexandersson.worklog.helpers;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;

public class FXHelper {

    private static Logger LOGGER = LoggerFactory.getLogger(FXHelper.class);

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
            LOGGER.debug("Loading {} with {}", fxmlFile.toString().split("!")[1], controllerClass.getName());
        }

        FXMLLoader fxmlLoader = new FXMLLoader(fxmlFile);
        T controller = fxmlLoader.getController();
        Parent parent = null;
        try {
            parent = fxmlLoader.load();
        } catch (IOException e) {
            LOGGER.error("Could not load fxml file {}", fxmlFile.toString().split("!")[1], e);
        }

        return new Pair<>(controller, parent);
    }
}
