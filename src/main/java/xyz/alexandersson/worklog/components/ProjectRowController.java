/*
 * Copyright 2017 Dennis Alexandersson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package xyz.alexandersson.worklog.components;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import xyz.alexandersson.worklog.models.Project;
import xyz.alexandersson.worklog.models.ProjectAction;
import xyz.alexandersson.worklog.services.ProjectService;
import xyz.alexandersson.worklog.views.log.LogController;

import java.net.URL;
import java.util.ResourceBundle;

import static xyz.alexandersson.worklog.models.ProjectAction.*;

public class ProjectRowController implements Initializable {
    @FXML
    private ComboBox<Project> projectComboBox;
    @FXML
    private ComboBox<ProjectAction> projectActionComboBox;
    private LogController logController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void init() {
        ProjectService projectService = logController.getProjectService();
        projectComboBox.itemsProperty().bind(projectService.projectsProperty());

        projectActionComboBox.getItems().addAll(NEW, RENAME, DELETE);
        projectActionComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                switch (newValue) {
                    case NEW:
                        projectService.onAddProject(this, projectComboBox.getScene().getWindow());
                        break;
                    case RENAME:
                        projectService.onEditProject(this, projectComboBox.getSelectionModel().getSelectedItem(), projectComboBox.getScene().getWindow());
                        break;
                    case DELETE:
                        projectService.onDeleteProject(this, projectComboBox.getSelectionModel().getSelectedItem(), projectComboBox.getScene().getWindow());
                        break;
                    default:
                        break;
                }
                Platform.runLater(() -> projectActionComboBox.getSelectionModel().clearSelection());
            }
        });
    }

    public void select(Project project) {
        projectComboBox.getSelectionModel().select(project);
    }

    public void clear(Project project) {
        projectComboBox.getSelectionModel().clearSelection();
        projectComboBox.getItems().remove(project);
    }

    public Project getProject() {
        return projectComboBox.getValue();
    }

    public void setLogController(LogController logController) {
        this.logController = logController;
    }
}
