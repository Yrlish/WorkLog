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

package xyz.alexandersson.worklog;

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
