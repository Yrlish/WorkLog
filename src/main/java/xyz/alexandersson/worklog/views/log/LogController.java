package xyz.alexandersson.worklog.views.log;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import xyz.alexandersson.worklog.helpers.DatabaseHelper;
import xyz.alexandersson.worklog.models.LogEntry;
import xyz.alexandersson.worklog.models.Project;
import xyz.alexandersson.worklog.models.TotalEntry;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class LogController implements Initializable {

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
    private Button logBtn;
    @FXML
    private TableView<LogEntry> logHistoryTable;
    @FXML
    private TableColumn<LogEntry, LocalDate> historyDateColumn;
    @FXML
    private TableColumn<LogEntry, Project> historyProjectColumn;
    @FXML
    private TableColumn<LogEntry, LocalTime> historyStartColumn;
    @FXML
    private TableColumn<LogEntry, LocalTime> historyStopColumn;
    @FXML
    private TableView<TotalEntry> logTotalTable;
    @FXML
    private TableColumn<TotalEntry, LocalDate> totalDateColumn;
    @FXML
    private TableColumn<TotalEntry, Project> totalProjectColumn;
    @FXML
    private TableColumn<TotalEntry, Double> totalWorkColumn;

    private ListProperty<Project> projects = new SimpleListProperty<>(FXCollections.observableArrayList());

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        startTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty() && !newValue.matches("^(00?[0-9]|1[0-9]|2[0-3]):([0-5][0-9])$")) {
                if (newValue.matches("^(00?[0-9]|1[0-9]|2[0-3])$")) {
                    startTextField.setText(newValue + ":");
                }

                startTextField.setStyle("-fx-text-box-border: red; -fx-focus-color: red;");
            } else {
                startTextField.setStyle("");
            }
        });
        stopTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty() && !newValue.matches("^(00?[0-9]|1[0-9]|2[0-3]):([0-5][0-9])$")) {
                if (newValue.matches("^(00?[0-9]|1[0-9]|2[0-3])$")) {
                    stopTextField.setText(newValue + ":");
                }

                stopTextField.setStyle("-fx-text-box-border: red; -fx-focus-color: red;");
            } else {
                stopTextField.setStyle("");
            }
        });

        historyDateColumn.setCellValueFactory(p -> new SimpleObjectProperty<>(p.getValue().getDate()));
        historyProjectColumn.setCellValueFactory(p -> new SimpleObjectProperty<>(p.getValue().getProject()));
        historyStartColumn.setCellValueFactory(p -> new SimpleObjectProperty<>(p.getValue().getStartTime()));
        historyStopColumn.setCellValueFactory(p -> new SimpleObjectProperty<>(p.getValue().getStopTime()));

        totalDateColumn.setCellValueFactory(p -> new SimpleObjectProperty<>(p.getValue().getDate()));
        totalProjectColumn.setCellValueFactory(p -> new SimpleObjectProperty<>(p.getValue().getProject()));
        totalWorkColumn.setCellValueFactory(p -> new SimpleObjectProperty<>(p.getValue().getTotalWork()));

        logHistoryTable.getItems().addListener((ListChangeListener<? super LogEntry>) c -> {
            c.next();
            if (c.wasAdded()) {
                logHistoryTable.sort();
            }
        });
        logHistoryTable.getSortOrder().add(historyDateColumn);
        logHistoryTable.getSortOrder().add(historyStartColumn);
        logTotalTable.getSortOrder().add(totalDateColumn);
        logTotalTable.getSortOrder().add(totalProjectColumn);

        projectComboBox.itemsProperty().bind(projects);
        datePicker.setValue(LocalDate.now());
        logBtn.setOnAction(event -> onLog());
        addProject.setOnAction(event -> onAddProject());

        projects.addAll(DatabaseHelper.getProjects());
        logHistoryTable.getItems().addAll(DatabaseHelper.getLogEntries());
        recalculateTotal();
    }

    private void onAddProject() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New project");
        dialog.setHeaderText("Name of the new project");

        dialog.showAndWait().ifPresent(str -> {
            Project project = new Project(str);

            projects.add(project);
            DatabaseHelper.saveProject(project);

            projectComboBox.getSelectionModel().select(project);
        });
    }

    private void onLog() {
        LogEntry logEntry = new LogEntry();
        logEntry.setDate(datePicker.getValue());
        logEntry.setProject(projectComboBox.getValue());
        logEntry.setStartTime(LocalTime.parse(startTextField.getText()));
        logEntry.setStopTime(LocalTime.parse(stopTextField.getText()));
        logEntry.setComment(commentArea.getText());

        logHistoryTable.getItems().add(logEntry);
        DatabaseHelper.saveLogEntry(logEntry);

        recalculateTotal();

        resetForm();
        startTextField.requestFocus();
    }

    private void recalculateTotal() {
        logTotalTable.getItems().clear();
        Map<LocalDate, Map<Project, Double>> map = new HashMap<>();

        for (LogEntry logEntry : logHistoryTable.getItems()) {
            Duration duration = Duration.between(logEntry.getStartTime(), logEntry.getStopTime());
            double hrs = duration.getSeconds() / 60.0 / 60.0;

            Map<Project, Double> map2 = map.getOrDefault(logEntry.getDate(), new HashMap<>());
            map2.put(logEntry.getProject(), map2.getOrDefault(logEntry.getProject(), 0D) + hrs);
            map.put(logEntry.getDate(), map2);
        }

        for (Map.Entry<LocalDate, Map<Project, Double>> entry : map.entrySet()) {
            for (Map.Entry<Project, Double> entry2 : entry.getValue().entrySet()) {
                TotalEntry totalEntry = new TotalEntry();
                totalEntry.setDate(entry.getKey());
                totalEntry.setProject(entry2.getKey());
                totalEntry.setTotalWork(entry2.getValue());
                logTotalTable.getItems().add(totalEntry);
            }
        }
    }

    private void resetForm() {
        startTextField.setText("");
        stopTextField.setText("");
    }
}
