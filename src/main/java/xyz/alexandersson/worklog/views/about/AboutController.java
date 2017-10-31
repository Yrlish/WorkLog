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
    private Hyperlink mitLink;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        appTitle.setText(AppVersion.getAppName());
        appVersion.setText(AppVersion.getVersion());
        appBuild.setText(AppVersion.getBuildNumber());

        LocalDateTime buildDateTime = AppVersion.getBuildTime().toLocalDateTime();
        appBuildDate.setText(String.format("%s %s", buildDateTime.toLocalDate(), buildDateTime.toLocalTime()));

        mitLink.setOnAction(event -> {
            WorkLog.getApplicationHostServices().showDocument("https://opensource.org/licenses/MIT");
        });
    }
}
