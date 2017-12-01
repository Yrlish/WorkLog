/*
 *  Copyright (c) 2017 Dennis Alexandersson
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package xyz.alexandersson.worklog.components;

import javafx.scene.control.TableCell;
import xyz.alexandersson.worklog.helpers.TimeHelper;

import java.text.DecimalFormat;

public class NonDecimalTableCell<S> extends TableCell<S, Double> {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");

    @Override
    protected void updateItem(Double item, boolean empty) {
        super.updateItem(item, empty);

        if (item != null || !empty) {
            setText(TimeHelper.hourDecimalToString(item));
        } else {
            setText(null);
        }
    }
}
