package nl.hanze.raspberryprocessor.Processing;

import nl.hanze.raspberryprocessor.Data.MeasurementInputQueue;
import nl.hanze.raspberryprocessor.Data.StationQueue;
import nl.hanze.raspberryprocessor.Output.OutputHandler;

//import java.util.Hashtable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StationQueueController implements Runnable {

    private ConcurrentHashMap<Integer, StationQueue> stationQueues;
    private MeasurementInputQueue inputQueue;
    private OutputHandler outputHandler;

    private ExecutorService threadPool;

    public StationQueueController(MeasurementInputQueue inputQueue) {
        stationQueues = new ConcurrentHashMap<>(8000);
        this.threadPool = Executors.newCachedThreadPool();
        outputHandler = new OutputHandler();
        this.inputQueue = inputQueue;
    }

    @Override
    public void run() {
        addStationQueueHandler();
        while(true) {
            if (inputQueue.getSize() > 10000) {
                addStationQueueHandler();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void addStationQueueHandler() {
        StationQueueHandler stationQueueHandler = new StationQueueHandler(stationQueues, inputQueue, outputHandler);
        Thread stationQueueHandlerThread = new Thread(stationQueueHandler);
        threadPool.execute(stationQueueHandlerThread);
    }
}
