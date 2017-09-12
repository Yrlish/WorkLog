package xyz.alexandersson.worklog.views.edit;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.alexandersson.worklog.TextFieldTimeChangeListener;
import xyz.alexandersson.worklog.helpers.DatabaseHelper;
import xyz.alexandersson.worklog.models.LogEntry;
import xyz.alexandersson.worklog.models.Project;
import xyz.alexandersson.worklog.views.log.LogController;

import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class EditEntryController implements Initializable {
    private static Logger LOGGER = LoggerFactory.getLogger(EditEntryController.class);
    @FXML
    private DatePicker datePicker;
    @FXML
    private ComboBox<Project> projectComboBox;
    @FXML
    private Button addProject;
    @FXML
    private TextField startTextField;
    @FXML
    private TextField stopTextField;
    @FXML
    private TextArea commentArea;
    @FXML
    private Button saveBtn;

    private LogEntry logEntry;
    private LogController logController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        startTextField.textProperty().addListener(new TextFieldTimeChangeListener(startTextField));
        stopTextField.textProperty().addListener(new TextFieldTimeChangeListener(stopTextField));

        saveBtn.setOnAction(event -> {
            onSave();
            ((Stage) saveBtn.getScene().getWindow()).close();
        });
    }

    private void onSave() {
        logEntry.setDate(datePicker.getValue());
        logEntry.setProject(projectComboBox.getValue());
        logEntry.setStartTime(LocalTime.parse(startTextField.getText()));
        logEntry.setStopTime(LocalTime.parse(stopTextField.getText()));
        logEntry.setComment(commentArea.getText());
        DatabaseHelper.saveUpdateLogEntry(logEntry);
        logController.reloadTables();
    }

    private void fill() {
        datePicker.setValue(logEntry.getDate());
        projectComboBox.getSelectionModel().select(logEntry.getProject());
        startTextField.setText(logEntry.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        stopTextField.setText(logEntry.getStopTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        commentArea.setText(logEntry.getComment());
    }

    public LogEntry getLogEntry() {
        return logEntry;
    }

    public void setLogEntry(LogEntry logEntry) {
        this.logEntry = logEntry;
        fill();
    }

    public void setLogController(LogController logController) {
        this.logController = logController;
    }
}
