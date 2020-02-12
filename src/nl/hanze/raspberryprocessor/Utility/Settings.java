package nl.hanze.raspberryprocessor.Utility;

public class Settings {
    public boolean SelectSeconds;
    public int SelectSecondsValue;
    public boolean Mute;
    public int QueueSize = 30;
    public int Port = 7789;

    public Settings(String[] args) {
        boolean readingSelectSecond = false;
        boolean readingQueueSize = false;
        boolean readingPort = false;

        for (String s: args) {
            if (s.equals("-s")) {
                readingSelectSecond = true;
                SelectSeconds = true;
                continue;
            } else if (readingSelectSecond) {
                SelectSecondsValue = Integer.valueOf(s);
                readingSelectSecond = false;
                continue;
            } else if (s.equals("--mute") || s.equals("-m")) {
                Mute = true;
                continue;
            } else if (s.equals("-q")) {
                readingQueueSize = true;
                continue;
            } else if (readingQueueSize) {
                readingQueueSize = false;
                QueueSize = Integer.valueOf(s);
                continue;
            } else if (s.equals("-p")) {
                readingQueueSize = true;
                continue;
            } else if (readingPort) {
                readingPort = false;
                Port = Integer.valueOf(s);
                continue;
            } else {
                throw new RuntimeException("Invalid parameter - " + s);
            }
        }
    }
}

