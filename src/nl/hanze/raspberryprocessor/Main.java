package nl.hanze.raspberryprocessor;

import nl.hanze.raspberryprocessor.Data.MeasurementInputQueue;
import nl.hanze.raspberryprocessor.Data.StationQueue;
import nl.hanze.raspberryprocessor.Input.WeatherServer;
import nl.hanze.raspberryprocessor.Output.OutputHandler;
import nl.hanze.raspberryprocessor.Processing.StationQueueController;
import nl.hanze.raspberryprocessor.Processing.StationQueueHandler;
import nl.hanze.raspberryprocessor.Utility.SemaphoreInteger;
import nl.hanze.raspberryprocessor.Utility.Settings;

import java.util.Hashtable;

public class Main {
    public static final long serialVersionUID=1L;
    public static Settings Settings;

    public static void main(String[] args) {
        Settings = new Settings(args);
        Controller controller = new Controller();
    }
}

class Controller {
    private MeasurementInputQueue inputQueue;
    private SemaphoreInteger connectionCount;

    private DebugThread debugThread;
    private WeatherServer weatherServer;
    private StationQueueController stationQueueController;

    Controller() {
        inputQueue = new MeasurementInputQueue();
        connectionCount = new SemaphoreInteger(0);

        weatherServer = new WeatherServer(inputQueue, connectionCount);
        Thread weatherServerThread = new Thread(weatherServer);
        weatherServerThread.start();

        //stationQueueController = new StationQueueController(inputQueue);
        //Thread stationQueueControllerThread = new Thread(stationQueueController);
        //stationQueueControllerThread.start();

        debugThread = new DebugThread(inputQueue, connectionCount);
        Thread debugThreadThread = new Thread(debugThread);
        debugThreadThread.start();

        if (Main.Settings.Mute) {
            System.out.println("Started RaspberryProcessor. Output = disabled");

            System.setOut(new java.io.PrintStream(new java.io.OutputStream() {
                @Override public void write(int b) {}
            }) {
                @Override public void flush() {}
                @Override public void close() {}
                @Override public void write(int b) {}
                @Override public void write(byte[] b) {}
                @Override public void write(byte[] buf, int off, int len) {}
                @Override public void print(boolean b) {}
                @Override public void print(char c) {}
                @Override public void print(int i) {}
                @Override public void print(long l) {}
                @Override public void print(float f) {}
                @Override public void print(double d) {}
                @Override public void print(char[] s) {}
                @Override public void print(String s) {}
                @Override public void print(Object obj) {}
                @Override public void println() {}
                @Override public void println(boolean x) {}
                @Override public void println(char x) {}
                @Override public void println(int x) {}
                @Override public void println(long x) {}
                @Override public void println(float x) {}
                @Override public void println(double x) {}
                @Override public void println(char[] x) {}
                @Override public void println(String x) {}
                @Override public void println(Object x) {}
                @Override public java.io.PrintStream printf(String format, Object... args) { return this; }
                @Override public java.io.PrintStream printf(java.util.Locale l, String format, Object... args) { return this; }
                @Override public java.io.PrintStream format(String format, Object... args) { return this; }
                @Override public java.io.PrintStream format(java.util.Locale l, String format, Object... args) { return this; }
                @Override public java.io.PrintStream append(CharSequence csq) { return this; }
                @Override public java.io.PrintStream append(CharSequence csq, int start, int end) { return this; }
                @Override public java.io.PrintStream append(char c) { return this; }
            });
       }
   }
}

