package nl.hanze.raspberryprocessor.Utility;

public class Settings {
    public boolean SelectSeconds;
    public int SelectSecondsValue;
    public boolean Mute = false;
    public int QueueSize = 30;
    public int Port = 7789;
    public int MaxConnections = 800;

    public Settings(String[] args) {
        boolean readingSelectSecond = false;
        boolean readingQueueSize = false;
        boolean readingPort = false;
        boolean readingMaxConnections = false;

        for (String s: args) {
            if (s.equals("-s")) {
                readingSelectSecond = true;
                SelectSeconds = true;
                continue;
            } else if (readingSelectSecond) {
                SelectSecondsValue = Integer.parseInt(s);
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
                QueueSize = Integer.parseInt(s);
                continue;
            } else if (s.equals("-p") || s.equals("--port")) {
                readingPort = true;
                continue;
            } else if (readingPort) {
                readingPort = false;
                Port = Integer.parseInt(s);
                continue;
            } else if (s.equals("-c")) {
                readingMaxConnections = true;
                continue;
            } else if (readingMaxConnections) {
                readingMaxConnections = false;
                MaxConnections = Integer.parseInt(s);
                continue;
            } else {
                throw new RuntimeException("Invalid parameter - " + s);
            }
        }
    }
}

