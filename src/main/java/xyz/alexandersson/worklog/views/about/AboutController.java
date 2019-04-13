/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package xyz.alexandersson.worklog.views.about;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.alexandersson.worklog.AppVersion;
import xyz.alexandersson.worklog.WorkLog;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class AboutController implements Initializable {
    private static final Logger LOGGER = LoggerFactory.getLogger(AboutController.class);

    @FXML
    private Label appTitle;
    @FXML
    private Label appVersion;
    @FXML
    private Label appBuild;
    @FXML
    private Label appBuildDate;
    @FXML
    private Hyperlink homepageLink;
    @FXML
    private Hyperlink licenseLink;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        appTitle.setText(AppVersion.getAppName());
        appVersion.setText(AppVersion.getVersion());
        appBuild.setText(AppVersion.getBuildNumber());

        LocalDateTime buildDateTime = AppVersion.getBuildTime().toLocalDateTime();
        appBuildDate.setText(String.format("%s %s", buildDateTime.toLocalDate(), buildDateTime.toLocalTime()));

        homepageLink.setOnAction(event -> WorkLog.getApplicationHostServices().showDocument("https://dennis.alexandersson.xyz"));
        licenseLink.setOnAction(event -> WorkLog.getApplicationHostServices().showDocument("https://opensource.org/licenses/MPL-2.0"));
    }
}
