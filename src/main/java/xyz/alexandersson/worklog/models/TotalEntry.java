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
        return Math.round(totalWork * 4) / 4.0;
    }
}
