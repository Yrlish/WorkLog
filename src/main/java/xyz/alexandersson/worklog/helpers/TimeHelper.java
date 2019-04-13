/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package xyz.alexandersson.worklog.helpers;

public class TimeHelper {
    public static String hourDecimalToString(Double time) {
        int hours = (int) Math.floor(time);
        int minutes = (int) Math.round((time * 60) % 60);

        return String.format("%dh %dm", hours, minutes);
    }
}
