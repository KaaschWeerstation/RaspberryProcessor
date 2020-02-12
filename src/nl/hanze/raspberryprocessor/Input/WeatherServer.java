package nl.hanze.raspberryprocessor.Input;

import nl.hanze.raspberryprocessor.Data.MeasurementInputQueue;
import nl.hanze.raspberryprocessor.Main;
import nl.hanze.raspberryprocessor.Utility.SemaphoreInteger;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WeatherServer implements Runnable {

    private static int Port = Main.Settings.Port;
    private static final int maximumConnections = 800;

    private MeasurementInputQueue inputQueue;
    private SemaphoreInteger connectionCount;

    private ExecutorService threadPool;
    private ServerSocket serverSocket;


    public WeatherServer(MeasurementInputQueue inputQueue, SemaphoreInteger connectionCount) {
        this.inputQueue = inputQueue;
        this.connectionCount = connectionCount;

        this.threadPool = Executors.newCachedThreadPool();
        try {
            this.serverSocket = new ServerSocket(Port);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException("WeatherServer - Couldn't start a serverSocket!");
        }
    }

    @Override
    public void run() {
        boolean doWhile = true;
        while (doWhile) {
            try {
                Socket connection = serverSocket.accept();
                //int count = connectionCount.get();

                //if (count < maximumConnections) {
                    //System.out.println("Connection accepted - Num: " + (count + 1));
                //TODO: Preinitialize Threads?
                Thread thread = new Thread(new ParallelInputSocket(inputQueue, connection, connectionCount));

                threadPool.execute(thread);
                connectionCount.increment();
//                } else {
//                    System.out.println("Connection refused");
//                }
            } catch (Exception e) {
                e.printStackTrace();
                doWhile = false;
            }
        }
        throw new IllegalStateException("WeatherServer - Exited");
    }


}
