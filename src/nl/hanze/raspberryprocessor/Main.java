package nl.hanze.raspberryprocessor;

import nl.hanze.raspberryprocessor.Data.MeasurementInputQueue;
import nl.hanze.raspberryprocessor.Data.StationQueue;
import nl.hanze.raspberryprocessor.Input.WeatherServer;
import nl.hanze.raspberryprocessor.Output.OutputHandler;
import nl.hanze.raspberryprocessor.Processing.StationQueueHandler;
import nl.hanze.raspberryprocessor.Utility.SemaphoreInteger;

import java.util.Hashtable;

public class Main {
    public static final long serialVersionUID=1L;
   // public static final byte[] serialVersionUID2 = ByteConversion.longToBytes(serialVersionUID);


    public static void main(String[] args) {
        Controller controller = new Controller();
    }
}

class Controller {
    private MeasurementInputQueue inputQueue;
    private SemaphoreInteger connectionCount;
    private Hashtable<Integer, StationQueue> stationQueues;

    private DebugThread debugThread;
    private WeatherServer weatherServer;
    private StationQueueHandler stationQueueHandler;
    private OutputHandler outputHandler;

    Controller() {
       inputQueue = new MeasurementInputQueue();
       connectionCount = new SemaphoreInteger(0);

       weatherServer = new WeatherServer(inputQueue, connectionCount);
       Thread weatherServerThread = new Thread(weatherServer);
       weatherServerThread.start();

       outputHandler = new OutputHandler();

       stationQueues = new Hashtable<Integer, StationQueue>(8000);
       stationQueueHandler = new StationQueueHandler(stationQueues, inputQueue, outputHandler);
       Thread stationQueueHandlerThread = new Thread(stationQueueHandler);
       stationQueueHandlerThread.start();

       debugThread = new DebugThread(inputQueue, connectionCount);
       Thread debugThreadThread = new Thread(debugThread);
       debugThreadThread.start();
   }
}