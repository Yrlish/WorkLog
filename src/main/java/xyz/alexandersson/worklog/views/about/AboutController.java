package xyz.alexandersson.worklog.views.about;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.alexandersson.worklog.AppVersion;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class AboutController implements Initializable {
    private static Logger LOGGER = LoggerFactory.getLogger(AboutController.class);

    @FXML
    private TextArea textArea;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        StringBuilder sb = new StringBuilder();
        sb.append("Version: ").append(AppVersion.getVersion()).append("\n");
        sb.append("Build: ").append(AppVersion.getBuildNumber()).append("\n");
        LocalDateTime buildDateTime = AppVersion.getBuildTime().toLocalDateTime();
        sb.append("Built: ").append(buildDateTime.toLocalDate()).append(" ").append(buildDateTime.toLocalTime());
        textArea.setText(sb.toString());
    }
}
