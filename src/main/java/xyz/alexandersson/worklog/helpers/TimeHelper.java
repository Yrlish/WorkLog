package xyz.alexandersson.worklog.helpers;

public class TimeHelper {
    public static String hourDecimalToString(Double time) {
        int hours = (int) Math.floor(time);
        int minutes = (int) Math.round((time * 60) % 60);

        return String.format("%dh %dm", hours, minutes);
    }
}
