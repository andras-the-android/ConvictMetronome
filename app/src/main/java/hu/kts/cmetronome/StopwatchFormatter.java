package hu.kts.cmetronome;

import java.util.Formatter;

/**
 * Created by andrasnemeth on 12/01/16.
 */
public class StopwatchFormatter {

    StringBuilder sb = new StringBuilder();
    Formatter formatter = new Formatter(sb);

    public String format(int totalSeconds) {
        int minutes = (totalSeconds / 60) % 60;
        int seconds = totalSeconds % 60;
        sb.setLength(0);
        formatter.format("%02d:%02d", minutes, seconds);
        return sb.toString();
    }

}
