package xyz.alexandersson.worklog.models;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
public class LogEntry {

    @Id
    @GeneratedValue
    private UUID id;
    private LocalDate date;
    @OneToOne
    @JoinColumn
    @LazyCollection(LazyCollectionOption.FALSE)
    private Project project;
    private LocalTime startTime;
    private LocalTime stopTime;
    private String comment;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getStopTime() {
        return stopTime;
    }

    public void setStopTime(LocalTime stopTime) {
        this.stopTime = stopTime;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
