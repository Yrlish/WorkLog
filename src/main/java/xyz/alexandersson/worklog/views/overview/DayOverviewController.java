/*
 *  Copyright (c) 2017 Dennis Alexandersson
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package xyz.alexandersson.worklog.views.overview;

import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.alexandersson.worklog.helpers.TimeHelper;
import xyz.alexandersson.worklog.models.Project;
import xyz.alexandersson.worklog.models.TotalEntry;

import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.Map;
import java.util.ResourceBundle;

public class DayOverviewController implements Initializable {
    private static final Logger LOGGER = LoggerFactory.getLogger(DayOverviewController.class);
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");

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
        decimalColumn.setCellFactory(param -> new TableCell<TotalEntry, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);

                if (item != null || !empty) {
                    setText(DECIMAL_FORMAT.format(item));
                } else {
                    setText(null);
                }
            }
        });
        nonDecimalColumn.setCellValueFactory(p -> new SimpleObjectProperty<>(p.getValue().getTotalWork()));
        nonDecimalColumn.setCellFactory(param -> new TableCell<TotalEntry, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);

                if (item != null || !empty) {
                    setText(TimeHelper.hourDecimalToString(item));
                } else {
                    setText(null);
                }
            }
        });

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

        for (Map.Entry<LocalDate, Map<Project, Double>> entry : totalEntryMap.entrySet()) {
            for (Map.Entry<Project, Double> entry2 : entry.getValue().entrySet()) {
                TotalEntry totalEntry = new TotalEntry();
                totalEntry.setDate(entry.getKey());
                totalEntry.setProject(entry2.getKey());
                totalEntry.setTotalWork(entry2.getValue());

                if (totalEntry.getDate().isEqual(date)) {
                    dayOverviewTable.getItems().add(totalEntry);
                }
            }
        }

        dayOverviewTable.sort();
    }

    public void setTotalEntryMap(Map<LocalDate, Map<Project, Double>> totalEntryMap) {
        this.totalEntryMap = totalEntryMap;
    }
}
