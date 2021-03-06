/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package xyz.alexandersson.worklog.views.log;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.alexandersson.worklog.components.CommentTableCell;
import xyz.alexandersson.worklog.components.DecimalTableCell;
import xyz.alexandersson.worklog.components.NonDecimalTableCell;
import xyz.alexandersson.worklog.components.ProjectRowController;
import xyz.alexandersson.worklog.converters.ISOLocalDateStringConverter;
import xyz.alexandersson.worklog.helpers.DatabaseHelper;
import xyz.alexandersson.worklog.helpers.FXHelper;
import xyz.alexandersson.worklog.listeners.TextAreaTabListener;
import xyz.alexandersson.worklog.listeners.TextFieldTimeChangeListener;
import xyz.alexandersson.worklog.models.LogEntry;
import xyz.alexandersson.worklog.models.Project;
import xyz.alexandersson.worklog.models.TotalEntry;
import xyz.alexandersson.worklog.services.ProjectService;
import xyz.alexandersson.worklog.views.about.AboutController;
import xyz.alexandersson.worklog.views.edit.EditEntryController;
import xyz.alexandersson.worklog.views.overview.DayOverviewController;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import static xyz.alexandersson.worklog.helpers.FXHelper.*;

public class LogController implements Initializable {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogController.class);

    @FXML
    private MenuItem exportMenuItem;
    @FXML
    private MenuItem settingsMenuItem;
    @FXML
    private MenuItem exitMenuItem;
    @FXML
    private MenuItem aboutMenuItem;
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
    private TableColumn<LogEntry, String> historyCommentColumn;
    @FXML
    private TableView<TotalEntry> logTotalTable;
    @FXML
    private TableColumn<TotalEntry, LocalDate> totalDateColumn;
    @FXML
    private TableColumn<TotalEntry, Project> totalProjectColumn;
    @FXML
    private TableColumn<TotalEntry, Double> totalWorkDecimalColumn;
    @FXML
    private TableColumn<TotalEntry, Double> totalWorkNonDecimalColumn;

    private ProjectService projectService = new ProjectService(this);
    private Map<LocalDate, Map<Project, Double>> logTotalMap;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        exitMenuItem.setOnAction(event -> Platform.exit());
        aboutMenuItem.setOnAction(event -> {
            Pair<AboutController, Parent> about = FXHelper.loadFxml(AboutController.class);
            FXHelper.loadWindow(about.getValue(), "About", logHistoryTable.getScene().getWindow(), false);
        });

        startTextField.textProperty().addListener(new TextFieldTimeChangeListener(startTextField));
        stopTextField.textProperty().addListener(new TextFieldTimeChangeListener(stopTextField));
        commentArea.addEventFilter(KeyEvent.KEY_PRESSED, new TextAreaTabListener());

        historyDateColumn.setCellValueFactory(p -> new SimpleObjectProperty<>(p.getValue().getDate()));
        historyProjectColumn.setCellValueFactory(p -> new SimpleObjectProperty<>(p.getValue().getProject()));
        historyStartColumn.setCellValueFactory(p -> new SimpleObjectProperty<>(p.getValue().getStartTime()));
        historyStopColumn.setCellValueFactory(p -> new SimpleObjectProperty<>(p.getValue().getStopTime()));
        historyCommentColumn.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getComment()));
        historyCommentColumn.setCellFactory(p -> new CommentTableCell<>());
        addTooltipToTableColumnHeader(logHistoryTable, historyCommentColumn,
                new Tooltip("New lines is replaced with |"));

        totalDateColumn.setCellValueFactory(p -> new SimpleObjectProperty<>(p.getValue().getDate()));
        totalProjectColumn.setCellValueFactory(p -> new SimpleObjectProperty<>(p.getValue().getProject()));
        totalWorkDecimalColumn.setCellValueFactory(p -> new SimpleObjectProperty<>(p.getValue().getTotalWork()));
        totalWorkDecimalColumn.setCellFactory(param -> new DecimalTableCell<>());
        totalWorkNonDecimalColumn.setCellValueFactory(p -> new SimpleObjectProperty<>(p.getValue().getTotalWork()));
        totalWorkNonDecimalColumn.setCellFactory(param -> new NonDecimalTableCell<>());

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

        logHistoryTable.setOnKeyPressed(event -> {
            LogEntry logEntry = logHistoryTable.getSelectionModel().getSelectedItem();
            if (logEntry == null)
                return;

            switch (event.getCode()) {
                case ENTER:
                    onEdit(logEntry);
                    break;
                case DELETE:
                    onDelete(logEntry);
                    break;
                default:
                    break;
            }
        });

        logTotalTable.setRowFactory(tableView -> {
            final TableRow<TotalEntry> row = new TableRow<>();

            row.setOnMousePressed(event -> {
                TotalEntry selectedItem = row.getItem();
                if (event.isPrimaryButtonDown()
                        && event.getClickCount() == 2
                        && row.getItem() != null) {
                    onOpenDayOverview(selectedItem.getDate());
                }
            });

            return row;
        });

        logTotalTable.setOnKeyPressed(event -> {
            TotalEntry totalEntry = logTotalTable.getSelectionModel().getSelectedItem();
            if (totalEntry == null)
                return;

            switch (event.getCode()) {
                case ENTER:
                    onOpenDayOverview(totalEntry.getDate());
                    break;
                default:
                    break;
            }
        });

        logHistoryTable.getSortOrder().add(historyDateColumn);
        logHistoryTable.getSortOrder().add(historyStartColumn);
        logTotalTable.getSortOrder().add(totalDateColumn);
        logTotalTable.getSortOrder().add(totalProjectColumn);

        projectRowController.setLogController(this);
        projectRowController.init();

        datePicker.setValue(LocalDate.now());
        datePicker.setConverter(new ISOLocalDateStringConverter());
        logBtn.setOnAction(event -> onLog());

        reloadData();
    }

    private void onLog() {
        if (validateLogForm()) {
            LogEntry logEntry = new LogEntry();
            logEntry.setDate(datePicker.getValue());
            logEntry.setProject(projectRowController.getProject());
            logEntry.setStartTime(LocalTime.parse(startTextField.getText()));
            if (!stopTextField.getText().isEmpty()) {
                logEntry.setStopTime(LocalTime.parse(stopTextField.getText()));
            }
            logEntry.setComment(commentArea.getText());

            logHistoryTable.getItems().add(logEntry);
            logHistoryTable.sort();
            DatabaseHelper.saveUpdateLogEntry(logEntry);

            recalculateTotal();

            resetForm();
            startTextField.requestFocus();
        }
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
                LocalTime stop = LocalTime.parse(stopTextField.getText());

                LocalTime start = LocalTime.parse(startTextField.getText());

                if (stop.isBefore(start)) {
                    showErrorAlert("Log error", "Log error", "Cannot set stop time before start time");
                    return false;
                }
            } catch (DateTimeParseException ex) {
                LOGGER.error("Cannot parse stop time field", ex);
                showErrorAlert("Log error", "Log error", "Stop time is not valid");
                return false;
            }
        }

        return true;
    }

    private void onEdit(LogEntry logEntry) {
        Pair<EditEntryController, Parent> parentPair = FXHelper.loadFxml(EditEntryController.class);
        EditEntryController controller = parentPair.getKey();
        controller.setLogController(this);
        controller.setLogEntry(logEntry);

        FXHelper.loadWindow(parentPair.getValue(), "Edit entry", logHistoryTable.getScene().getWindow(), false);
    }

    private void onDelete(LogEntry logEntry) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setContentText("Are you sure you want to delete this log entry?");

        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);

        ((Button) alert.getDialogPane().lookupButton(ButtonType.YES)).setDefaultButton(false);
        ((Button) alert.getDialogPane().lookupButton(ButtonType.NO)).setDefaultButton(true);

        alert.showAndWait().ifPresent(buttonType -> {
            if (buttonType == ButtonType.YES) {
                DatabaseHelper.deleteLogEntry(logEntry);
                logHistoryTable.getItems().remove(logEntry);
                recalculateTotal();
            }
        });
    }

    private void onOpenDayOverview(LocalDate date) {
        Pair<DayOverviewController, Parent> parentPair = FXHelper.loadFxml(DayOverviewController.class);
        DayOverviewController controller = parentPair.getKey();
        controller.setTotalEntryMap(logTotalMap);
        controller.loadDate(date);

        loadWindow(parentPair.getValue(), "Day Overview", logTotalTable.getScene().getWindow(), false);
    }

    private void recalculateTotal() {
        logTotalTable.getItems().clear();
        logTotalMap = new HashMap<>();

        for (LogEntry logEntry : logHistoryTable.getItems()) {
            if (logEntry.getStopTime() == null) {
                continue;
            }

            Duration duration = Duration.between(logEntry.getStartTime(), logEntry.getStopTime());
            double hrs = duration.getSeconds() / 60.0 / 60.0;

            Map<Project, Double> map2 = logTotalMap.getOrDefault(logEntry.getDate(), new HashMap<>());
            map2.put(logEntry.getProject(), map2.getOrDefault(logEntry.getProject(), 0D) + hrs);
            logTotalMap.put(logEntry.getDate(), map2);
        }

        for (Map.Entry<LocalDate, Map<Project, Double>> entry : logTotalMap.entrySet()) {
            for (Map.Entry<Project, Double> entry2 : entry.getValue().entrySet()) {
                TotalEntry totalEntry = new TotalEntry();
                totalEntry.setDate(entry.getKey());
                totalEntry.setProject(entry2.getKey());
                totalEntry.setTotalWork(entry2.getValue());
                logTotalTable.getItems().add(totalEntry);
            }
        }

        logTotalTable.sort();
    }

    private void resetForm() {
        startTextField.setText("");
        stopTextField.setText("");
        commentArea.setText("");
    }

    public void reloadTables() {
        logHistoryTable.sort();
        logHistoryTable.refresh();
        recalculateTotal();
    }

    public void reloadData() {
        projectService.reload();
        logHistoryTable.getItems().clear();
        logHistoryTable.getItems().addAll(DatabaseHelper.getLogEntries());
        recalculateTotal();
    }

    public ProjectService getProjectService() {
        return projectService;
    }

    public ObservableList<LogEntry> getLogEntries() {
        return logHistoryTable.getItems();
    }

}
