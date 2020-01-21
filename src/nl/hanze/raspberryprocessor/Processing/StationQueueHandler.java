package nl.hanze.raspberryprocessor.Processing;

import nl.hanze.raspberryprocessor.Data.Measurement;
import nl.hanze.raspberryprocessor.Data.MeasurementInputQueue;
import nl.hanze.raspberryprocessor.Data.StationQueue;
import nl.hanze.raspberryprocessor.Output.OutputHandler;

import java.util.Hashtable;

/**
 * Takes Measurements out of the MeasurementInputQueue and puts them in a StationQueue for their
 * respective stationId. This thread currently uses a synchronised Hashtable, which seems to be
 * enough to empty out the input queue when getting input from 800 sockets.
 */
public class StationQueueHandler implements Runnable {

    private Hashtable<Integer, StationQueue> stationQueues;
    private MeasurementInputQueue measurementInputQueue;
    private OutputHandler outputHandler;

    public StationQueueHandler(Hashtable<Integer, StationQueue> stationQueues, MeasurementInputQueue measurementInputQueue, OutputHandler outputHandler) {
        this.stationQueues = stationQueues;
        this.measurementInputQueue = measurementInputQueue;
        this.outputHandler = outputHandler;
    }

    @Override
    public void run() {
        while (true) {
            Measurement measurement = measurementInputQueue.poll();
            if (measurement != null) {
                int stationId = measurement.getStationId();

                if (stationQueues.containsKey(stationId)) {
                    StationQueue stationQueue = stationQueues.get(stationId);
                    try {
                        stationQueue.Queue(measurement);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    StationQueue stationQueue = new StationQueue(stationId);
                    stationQueues.put(stationId, stationQueue);
                    outputHandler.notifyNewStation(stationId, stationQueue);
                    try {
                        stationQueue.Queue(measurement);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}