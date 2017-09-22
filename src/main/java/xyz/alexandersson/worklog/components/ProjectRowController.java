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
