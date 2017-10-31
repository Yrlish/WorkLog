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

package xyz.alexandersson.worklog.services;

import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Modality;
import javafx.stage.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.alexandersson.worklog.components.ProjectRowController;
import xyz.alexandersson.worklog.helpers.DatabaseHelper;
import xyz.alexandersson.worklog.models.Project;
import xyz.alexandersson.worklog.views.log.LogController;

public class ProjectService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectService.class);

    private static ReadOnlyListProperty<Project> projects = new ReadOnlyListWrapper<>(FXCollections.observableArrayList());

    private LogController logController;

    public ProjectService(LogController logController) {
        this.logController = logController;
    }

    public void onAddProject(ProjectRowController prc, Window window) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.initOwner(window);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setTitle("New project");
        dialog.setHeaderText("Name of the new project");

        dialog.showAndWait().ifPresent(str -> {
            Project project = new Project(str);

            projects.add(project);
            DatabaseHelper.saveUpdateProject(project);

            prc.select(project);
        });
    }

    public void onEditProject(ProjectRowController prc, Project project, Window window) {
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

            logController.reloadData();

            prc.select(project);
        });
    }

    public void onDeleteProject(ProjectRowController prc, Project project, Window window) {
        if (project == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(window);
            alert.initModality(Modality.WINDOW_MODAL);
            alert.setHeaderText("No project selected");
            alert.setContentText("You need to select a project first.");
            alert.showAndWait();
            return;
        }

        boolean projectInUse = logController.getLogEntries().stream()
                .anyMatch(entry -> entry.getProject().equals(project));

        if (projectInUse) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(window);
            alert.initModality(Modality.WINDOW_MODAL);
            alert.setHeaderText("Cannot delete project");
            alert.setContentText("Cannot delete this project because it is being used.");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(window);
            alert.initModality(Modality.WINDOW_MODAL);
            alert.setContentText(String.format("Are you sure you want to delete project '%s'?", project.getName()));

            alert.getButtonTypes().clear();
            alert.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);

            ((Button) alert.getDialogPane().lookupButton(ButtonType.YES)).setDefaultButton(false);
            ((Button) alert.getDialogPane().lookupButton(ButtonType.NO)).setDefaultButton(true);

            alert.showAndWait().ifPresent(buttonType -> {
                if (buttonType == ButtonType.YES) {
                    prc.clear(project);
                    DatabaseHelper.deleteProject(project);
                }
            });
        }
    }

    public ReadOnlyListProperty<Project> projectsProperty() {
        return projects;
    }

    public void reload() {
        projects.clear();
        projects.addAll(DatabaseHelper.getProjects());
    }
}
