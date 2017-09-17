package xyz.alexandersson.worklog.views.edit;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.alexandersson.worklog.TextFieldTimeChangeListener;
import xyz.alexandersson.worklog.components.ProjectRowController;
import xyz.alexandersson.worklog.helpers.DatabaseHelper;
import xyz.alexandersson.worklog.models.LogEntry;
import xyz.alexandersson.worklog.views.log.LogController;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;

import static xyz.alexandersson.worklog.helpers.FXHelper.showErrorAlert;

public class EditEntryController implements Initializable {
    private static Logger LOGGER = LoggerFactory.getLogger(EditEntryController.class);
    @FXML
    private DatePicker datePicker;
    @FXML
    private ProjectRowController projectRowController;
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
            if (onSave()) {
                ((Stage) saveBtn.getScene().getWindow()).close();
            }
        });
    }

    private boolean onSave() {
        if (validateLogForm()) {
            logEntry.setDate(datePicker.getValue());
            logEntry.setProject(projectRowController.getProject());
            logEntry.setStartTime(LocalTime.parse(startTextField.getText()));
            logEntry.setStopTime(LocalTime.parse(stopTextField.getText()));
            logEntry.setComment(commentArea.getText());
            DatabaseHelper.saveUpdateLogEntry(logEntry);
            logController.reloadTables();
            return true;
        }
        return false;
    }

    private boolean validateLogForm() {
        if (!datePicker.getValue().isEqual(LocalDate.now())
                && !datePicker.getValue().isBefore(LocalDate.now())) {
            // Do not accept dates from the future
            showErrorAlert("Log error", "Log error", "Can only select today's date and dates from the past");
            return false;
        }

        if (projectRowController.getProject() == null) {
            // No project selected
            showErrorAlert("Log error", "Log error", "No project selected");
            return false;
        }

        if (!startTextField.getText().isEmpty()) {
            try {
                LocalTime.parse(startTextField.getText());
            } catch (DateTimeParseException ex) {
                LOGGER.error("Cannot parse start time field", ex);
                showErrorAlert("Log error", "Log error", "Start time is not valid");
                return false;
            }
        } else {
            showErrorAlert("Log error", "Log error", "Start time is empty");
            return false;
        }

        // Optional field
        if (!stopTextField.getText().isEmpty()) {
            try {
                LocalTime.parse(stopTextField.getText());
            } catch (DateTimeParseException ex) {
                LOGGER.error("Cannot parse stop time field", ex);
                showErrorAlert("Log error", "Log error", "Stop time is not valid");
                return false;
            }
        }

        return true;
    }

    private void fill() {
        datePicker.setValue(logEntry.getDate());
        projectRowController.select(logEntry.getProject());
        startTextField.setText(logEntry.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        stopTextField.setText(logEntry.getStopTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        commentArea.setText(logEntry.getComment());
    }

    public void setLogEntry(LogEntry logEntry) {
        this.logEntry = logEntry;
        fill();
    }

    public void setLogController(LogController logController) {
        this.logController = logController;
        projectRowController.setLogController(logController);
        projectRowController.init();
    }
}
