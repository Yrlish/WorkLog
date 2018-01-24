/*
 *  Copyright (c) 2017 Dennis Alexandersson
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package xyz.alexandersson.worklog.listeners;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TextFieldTimeChangeListener implements ChangeListener<String> {
    private static final Logger LOGGER = LoggerFactory.getLogger(TextFieldTimeChangeListener.class);
    private final TextField textField;

    public TextFieldTimeChangeListener(TextField textField) {
        this.textField = textField;
    }

    @Override
    public void changed(ObservableValue observable, String oldValue, String newValue) {
        if (!newValue.isEmpty() && !newValue.matches("^(00?[0-9]|1[0-9]|2[0-3]):([0-5][0-9])$")) {
            if (newValue.matches("^(00?[0-9]|1[0-9]|2[0-3])$")
                    && !oldValue.contains(":")) {
                textField.setText(newValue + ":");
            }

            textField.setStyle("-fx-text-box-border: red; -fx-focus-color: red;");
        } else {
            textField.setStyle("");
        }
    }
}
