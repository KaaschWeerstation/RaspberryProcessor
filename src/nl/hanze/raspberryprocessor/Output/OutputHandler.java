package nl.hanze.raspberryprocessor.Output;

import nl.hanze.raspberryprocessor.Data.StationQueue;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OutputHandler {

    public static final File destinationDirectory = new File("out/data");

    private ExecutorService threadPool;

    public OutputHandler() {
        if (!destinationDirectory.exists()) { destinationDirectory.mkdirs(); }
        this.threadPool = Executors.newCachedThreadPool();
    }

    public void notifyNewStation(int stationId, StationQueue stationQueue) {
        Thread thread = new Thread(new OutputThread(destinationDirectory, stationId, stationQueue));
        threadPool.execute(thread);
    }
}
