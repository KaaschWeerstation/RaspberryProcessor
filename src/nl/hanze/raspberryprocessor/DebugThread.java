package nl.hanze.raspberryprocessor;

import nl.hanze.raspberryprocessor.Data.MeasurementInputQueue;
import nl.hanze.raspberryprocessor.Utility.SemaphoreInteger;

public class DebugThread implements Runnable {

    private MeasurementInputQueue measurementInputQueue;
    private SemaphoreInteger semaphoreInteger;

    DebugThread(MeasurementInputQueue measurementInputQueue, SemaphoreInteger semaphoreInteger) {
        this.measurementInputQueue = measurementInputQueue;
        this.semaphoreInteger = semaphoreInteger;
    }

    public void run() {
        while(true) {
            try {
                Thread.sleep(1000);
                System.out.println("Queue size: " + measurementInputQueue.getSize());
                System.out.println("Connection count: " + semaphoreInteger.get());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
