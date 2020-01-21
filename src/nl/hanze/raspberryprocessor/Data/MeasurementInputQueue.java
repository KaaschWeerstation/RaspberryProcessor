package nl.hanze.raspberryprocessor.Data;

import java.util.concurrent.ConcurrentLinkedQueue;

public class MeasurementInputQueue {

    private ConcurrentLinkedQueue<Measurement> concurrentLinkedQueue;

    public MeasurementInputQueue() {
        this.concurrentLinkedQueue = new ConcurrentLinkedQueue<>();
    }

    public void add(Measurement measurement) {
        if (measurement != null) {
            concurrentLinkedQueue.add(measurement);
        } else {
            throw new NullPointerException("MeasurementInputQueue - Measurement is null");
        }
    }

    public Measurement get() {
        if (concurrentLinkedQueue.peek() != null) {
            return concurrentLinkedQueue.poll();
        } else {
            return null;
        }
    }

    public Measurement poll() {
        return concurrentLinkedQueue.poll();
    }

    public Measurement peek() {
        return concurrentLinkedQueue.peek();
    }

    public int getSize() {
        return concurrentLinkedQueue.size();
    }
}
