package xyz.alexandersson.worklog.helpers;

public class StringHelper {
    public static String removeLineBreaks(String str) {
        return str
                .trim()
                .replaceAll("\r\n", " | ")
                .replaceAll("\r", " | ")
                .replaceAll("\n", " | ");
    }
}
