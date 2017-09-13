package xyz.alexandersson.worklog.views.log;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.alexandersson.worklog.TextFieldTimeChangeListener;
import xyz.alexandersson.worklog.helpers.DatabaseHelper;
import xyz.alexandersson.worklog.helpers.FXHelper;
import xyz.alexandersson.worklog.models.LogEntry;
import xyz.alexandersson.worklog.models.Project;
import xyz.alexandersson.worklog.models.ProjectAction;
import xyz.alexandersson.worklog.models.TotalEntry;
import xyz.alexandersson.worklog.views.edit.EditEntryController;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import static xyz.alexandersson.worklog.models.ProjectAction.*;

public class LogController implements Initializable {

    private static Logger LOGGER = LoggerFactory.getLogger(LogController.class);

    @FXML
    private DatePicker datePicker;
    @FXML
    private ComboBox<Project> projectComboBox;
    @FXML
    private ComboBox<ProjectAction> projectActionComboBox;
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
        startTextField.textProperty().addListener(new TextFieldTimeChangeListener(startTextField));
        stopTextField.textProperty().addListener(new TextFieldTimeChangeListener(stopTextField));

        historyDateColumn.setCellValueFactory(p -> new SimpleObjectProperty<>(p.getValue().getDate()));
        historyProjectColumn.setCellValueFactory(p -> new SimpleObjectProperty<>(p.getValue().getProject()));
        historyStartColumn.setCellValueFactory(p -> new SimpleObjectProperty<>(p.getValue().getStartTime()));
        historyStopColumn.setCellValueFactory(p -> new SimpleObjectProperty<>(p.getValue().getStopTime()));

        totalDateColumn.setCellValueFactory(p -> new SimpleObjectProperty<>(p.getValue().getDate()));
        totalProjectColumn.setCellValueFactory(p -> new SimpleObjectProperty<>(p.getValue().getProject()));
        totalWorkColumn.setCellValueFactory(p -> new SimpleObjectProperty<>(p.getValue().getTotalWork()));

        logHistoryTable.getItems().addListener((ListChangeListener<? super LogEntry>) c -> {
            c.next();
            if (c.wasAdded() || c.wasReplaced() || c.wasUpdated()) {
                logHistoryTable.sort();
            }
        });

        logHistoryTable.setOnMousePressed(event -> {
            LogEntry selectedItem = logHistoryTable.getSelectionModel().getSelectedItem();
            if (event.isPrimaryButtonDown()
                    && event.getClickCount() == 2
                    && selectedItem != null) {
                onEdit(selectedItem);
            }
        });

        logHistoryTable.setRowFactory(tableView -> {
            final TableRow<LogEntry> row = new TableRow<>();
            final ContextMenu rowMenu = new ContextMenu();

            MenuItem editItem = new MenuItem("Edit");
            editItem.setOnAction(event -> onEdit(row.getItem()));

            MenuItem deleteItem = new MenuItem("Delete");
            deleteItem.setOnAction(event -> onDelete(row.getItem()));

            rowMenu.getItems().addAll(editItem, deleteItem);

            row.contextMenuProperty().bind(
                    Bindings.when(Bindings.isNotNull(row.itemProperty()))
                            .then(rowMenu)
                            .otherwise((ContextMenu) null));
            return row;
        });

        logHistoryTable.getSortOrder().add(historyDateColumn);
        logHistoryTable.getSortOrder().add(historyStartColumn);
        logTotalTable.getSortOrder().add(totalDateColumn);
        logTotalTable.getSortOrder().add(totalProjectColumn);

        projectComboBox.itemsProperty().bind(projects);
        datePicker.setValue(LocalDate.now());
        logBtn.setOnAction(event -> onLog());

        projectActionComboBox.getItems().addAll(NEW, RENAME, DELETE);
        projectActionComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                switch (newValue) {
                    case NEW:
                        onAddProject();
                        break;
                    case RENAME:
                        onEditProject(projectComboBox.getSelectionModel().getSelectedItem());
                        break;
                    case DELETE:
                        onDeleteProject(projectComboBox.getSelectionModel().getSelectedItem());
                        break;
                    default:
                        break;
                }
                Platform.runLater(() -> projectActionComboBox.getSelectionModel().clearSelection());
            }
        });

        reloadData();
    }

    private void reloadData() {
        projects.clear();
        projects.addAll(DatabaseHelper.getProjects());
        logHistoryTable.getItems().clear();
        logHistoryTable.getItems().addAll(DatabaseHelper.getLogEntries());
        recalculateTotal();
    }

    private void onAddProject() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.initOwner(projectActionComboBox.getScene().getWindow());
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setTitle("New project");
        dialog.setHeaderText("Name of the new project");

        dialog.showAndWait().ifPresent(str -> {
            Project project = new Project(str);

            projects.add(project);
            DatabaseHelper.saveUpdateProject(project);

            projectComboBox.getSelectionModel().select(project);
        });
    }

    private void onEditProject(Project project) {
        TextInputDialog dialog = new TextInputDialog(project.getName());
        dialog.initOwner(projectActionComboBox.getScene().getWindow());
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setTitle("Edit project");
        dialog.setHeaderText("Change the name of the project");

        dialog.showAndWait().ifPresent(str -> {
            project.setName(str);
            DatabaseHelper.saveUpdateProject(project);

            reloadData();

            projectComboBox.getSelectionModel().select(project);
        });
    }

    private void onDeleteProject(Project project) {
        if (project == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(projectActionComboBox.getScene().getWindow());
            alert.initModality(Modality.WINDOW_MODAL);
            alert.setHeaderText("No project selected");
            alert.setContentText("You need to select a project first.");
            alert.showAndWait();
            return;
        }

        boolean projectInUse = logHistoryTable.getItems().stream()
                .anyMatch(entry -> entry.getProject().equals(project));

        if (projectInUse) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(projectActionComboBox.getScene().getWindow());
            alert.initModality(Modality.WINDOW_MODAL);
            alert.setHeaderText("Cannot delete project");
            alert.setContentText("Cannot delete this project because it is being used.");
            alert.showAndWait();
        } else {
            projectComboBox.getSelectionModel().clearSelection();
            projectComboBox.getItems().remove(project);
            DatabaseHelper.deleteProject(project);
        }
    }

    private void onLog() {
        LogEntry logEntry = new LogEntry();
        logEntry.setDate(datePicker.getValue());
        logEntry.setProject(projectComboBox.getValue());
        logEntry.setStartTime(LocalTime.parse(startTextField.getText()));
        logEntry.setStopTime(LocalTime.parse(stopTextField.getText()));
        logEntry.setComment(commentArea.getText());

        logHistoryTable.getItems().add(logEntry);
        DatabaseHelper.saveUpdateLogEntry(logEntry);

        recalculateTotal();

        resetForm();
        startTextField.requestFocus();
    }

    private void onEdit(LogEntry logEntry) {
        Pair<EditEntryController, Parent> parentPair = FXHelper.loadFxml(EditEntryController.class);
        EditEntryController controller = parentPair.getKey();
        controller.setLogController(this);
        controller.setLogEntry(logEntry);

        FXHelper.loadWindow(parentPair.getValue(), "Edit entry", false);
    }

    private void onDelete(LogEntry logEntry) {
        DatabaseHelper.deleteLogEntry(logEntry);
        logHistoryTable.getItems().remove(logEntry);
        recalculateTotal();
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
        commentArea.setText("");
    }

    public void reloadTables() {
        logHistoryTable.refresh();
        recalculateTotal();
    }
}
