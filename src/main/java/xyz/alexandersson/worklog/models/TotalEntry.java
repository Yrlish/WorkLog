/*
 *  Copyright (c) 2017 Dennis Alexandersson
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package xyz.alexandersson.worklog.models;

import java.time.LocalDate;

public class TotalEntry {

    private LocalDate date;
    private Project project;
    private Double totalWork;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Double getTotalWork() {
        return totalWork;
    }

    public void setTotalWork(Double totalWork) {
        this.totalWork = totalWork;
    }

    public Double getRoundedTotalWork() {
        return Math.ceil(totalWork * 4) / 4.0;
    }
}
