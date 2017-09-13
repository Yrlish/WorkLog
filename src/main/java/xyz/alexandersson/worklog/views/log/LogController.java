package xyz.alexandersson.worklog.views.log;

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
import javafx.stage.Window;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.alexandersson.worklog.TextFieldTimeChangeListener;
import xyz.alexandersson.worklog.components.ProjectRowController;
import xyz.alexandersson.worklog.helpers.DatabaseHelper;
import xyz.alexandersson.worklog.helpers.FXHelper;
import xyz.alexandersson.worklog.models.LogEntry;
import xyz.alexandersson.worklog.models.Project;
import xyz.alexandersson.worklog.models.TotalEntry;
import xyz.alexandersson.worklog.views.edit.EditEntryController;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class LogController implements Initializable {

    private static Logger LOGGER = LoggerFactory.getLogger(LogController.class);

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

        logHistoryTable.setRowFactory(tableView -> {
            final TableRow<LogEntry> row = new TableRow<>();
            final ContextMenu rowMenu = new ContextMenu();

            row.setOnMousePressed(event -> {
                LogEntry selectedItem = row.getItem();
                if (event.isPrimaryButtonDown()
                        && event.getClickCount() == 2
                        && row.getItem() != null) {
                    onEdit(selectedItem);
                }
            });

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

        projectRowController.setLogController(this);
        projectRowController.init();

        datePicker.setValue(LocalDate.now());
        logBtn.setOnAction(event -> onLog());

        reloadData();
    }

    private void reloadData() {
        projects.clear();
        projects.addAll(DatabaseHelper.getProjects());
        logHistoryTable.getItems().clear();
        logHistoryTable.getItems().addAll(DatabaseHelper.getLogEntries());
        recalculateTotal();
    }

    public void onAddProject(Window window) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.initOwner(window);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setTitle("New project");
        dialog.setHeaderText("Name of the new project");

        dialog.showAndWait().ifPresent(str -> {
            Project project = new Project(str);

            projects.add(project);
            DatabaseHelper.saveUpdateProject(project);

            projectRowController.select(project);
        });
    }

    public void onEditProject(Project project, Window window) {
        if (project == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(window);
            alert.initModality(Modality.WINDOW_MODAL);
            alert.setHeaderText("No project selected");
            alert.setContentText("You need to select a project first.");
            alert.showAndWait();
            return;
        }

        TextInputDialog dialog = new TextInputDialog(project.getName());
        dialog.initOwner(window);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setTitle("Edit project");
        dialog.setHeaderText("Change the name of the project");

        dialog.showAndWait().ifPresent(str -> {
            project.setName(str);
            DatabaseHelper.saveUpdateProject(project);

            reloadData();

            projectRowController.select(project);
        });
    }

    public void onDeleteProject(Project project, Window window) {
        if (project == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(window);
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
            alert.initOwner(window);
            alert.initModality(Modality.WINDOW_MODAL);
            alert.setHeaderText("Cannot delete project");
            alert.setContentText("Cannot delete this project because it is being used.");
            alert.showAndWait();
        } else {
            projectRowController.clear(project);
            DatabaseHelper.deleteProject(project);
        }
    }

    private void onLog() {
        LogEntry logEntry = new LogEntry();
        logEntry.setDate(datePicker.getValue());
        logEntry.setProject(projectRowController.getProject());
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

        FXHelper.loadWindow(parentPair.getValue(), "Edit entry", logHistoryTable.getScene().getWindow(), false);
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

    public ListProperty<Project> projectsProperty() {
        return projects;
    }
}
