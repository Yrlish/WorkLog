/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package xyz.alexandersson.worklog.components;

import javafx.scene.control.TableCell;
import xyz.alexandersson.worklog.helpers.StringHelper;

public class CommentTableCell<S> extends TableCell<S, String> {

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);

        if (item != null || !empty) {
            setText(StringHelper.removeLineBreaks(item));
        } else {
            setText(null);
        }
    }
}
