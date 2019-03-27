/*
 * Copyright (c) 2019 Dennis Alexandersson
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package xyz.alexandersson.worklog.converters;

import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ISOLocalDateStringConverter extends StringConverter<LocalDate> {
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_DATE;

    @Override
    public String toString(LocalDate localDate) {
        if (localDate == null) {
            return "";
        }
        return dateTimeFormatter.format(localDate);
    }

    @Override
    public LocalDate fromString(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }
        return LocalDate.parse(dateString, dateTimeFormatter);
    }
}
