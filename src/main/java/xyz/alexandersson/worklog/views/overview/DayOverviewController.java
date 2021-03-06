/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package xyz.alexandersson.worklog.views.overview;

import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.alexandersson.worklog.components.DecimalTableCell;
import xyz.alexandersson.worklog.components.NonDecimalTableCell;
import xyz.alexandersson.worklog.models.Project;
import xyz.alexandersson.worklog.models.TotalEntry;

import java.net.URL;
import java.time.LocalDate;
import java.util.Map;
import java.util.ResourceBundle;

public class DayOverviewController implements Initializable {
    private static final Logger LOGGER = LoggerFactory.getLogger(DayOverviewController.class);

    @FXML
    private AnchorPane root;
    @FXML
    private Label dateLabel;
    @FXML
    private TableView<TotalEntry> dayOverviewTable;
    @FXML
    private TableColumn<TotalEntry, Project> projectColumn;
    @FXML
    private TableColumn<TotalEntry, Double> decimalColumn;
    @FXML
    private TableColumn<TotalEntry, Double> nonDecimalColumn;
    @FXML
    private Button previousDayBtn;
    @FXML
    private Button nextDayBtn;

    private Map<LocalDate, Map<Project, Double>> totalEntryMap;
    private LocalDate previousDay;
    private LocalDate nextDay;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        projectColumn.setCellValueFactory(p -> new SimpleObjectProperty<>(p.getValue().getProject()));
        decimalColumn.setCellValueFactory(p -> new SimpleObjectProperty<>(p.getValue().getTotalWork()));
        decimalColumn.setCellFactory(param -> new DecimalTableCell<>());
        nonDecimalColumn.setCellValueFactory(p -> new SimpleObjectProperty<>(p.getValue().getTotalWork()));
        nonDecimalColumn.setCellFactory(param -> new NonDecimalTableCell<>());

        dayOverviewTable.getSortOrder().clear();
        dayOverviewTable.getSortOrder().add(projectColumn);

        previousDayBtn.setOnAction(event -> loadDate(previousDay));
        nextDayBtn.setOnAction(event -> loadDate(nextDay));

        EventHandler<KeyEvent> keyEventHandler = event -> {
            switch (event.getCode()) {
                case ESCAPE:
                    ((Stage) dayOverviewTable.getScene().getWindow()).close();
                    break;
                case RIGHT:
                case PAGE_DOWN:
                    loadDate(nextDay);
                    break;
                case LEFT:
                case PAGE_UP:
                    loadDate(previousDay);
                    break;
                default:
                    break;
            }
        };
        root.setOnKeyPressed(keyEventHandler);
        dayOverviewTable.setOnKeyPressed(keyEventHandler);
    }

    public void loadDate(LocalDate date) {
        dayOverviewTable.getItems().clear();

        dateLabel.setText(date.toString());
        previousDay = date.minusDays(1);
        nextDay = date.plusDays(1);
        double totalDayWork = 0;

        for (Map.Entry<LocalDate, Map<Project, Double>> entry : totalEntryMap.entrySet()) {
            for (Map.Entry<Project, Double> entry2 : entry.getValue().entrySet()) {
                TotalEntry totalEntry = new TotalEntry();
                totalEntry.setDate(entry.getKey());
                totalEntry.setProject(entry2.getKey());
                totalEntry.setTotalWork(entry2.getValue());

                if (totalEntry.getDate().isEqual(date)) {
                    totalDayWork += entry2.getValue();
                    dayOverviewTable.getItems().add(totalEntry);
                }
            }
        }

        TotalEntry totalDay = new TotalEntry();
        totalDay.setProject(new Project("-- TOTAL WORK OF DAY"));
        totalDay.setTotalWork(totalDayWork);
        dayOverviewTable.getItems().add(totalDay);

        dayOverviewTable.sort();
    }

    public void setTotalEntryMap(Map<LocalDate, Map<Project, Double>> totalEntryMap) {
        this.totalEntryMap = totalEntryMap;
    }
}
