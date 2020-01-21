package nl.hanze.raspberryprocessor.Data;

import java.util.LinkedList;

/**
 * A Wrapper around a LinkedList meant to keep 30 measurements of a station in buffer.
 * TODO: Add missing data interpolation - Which should be done with this list
 */
public class StationQueue {

    private LinkedList<Measurement> linkedList;
    public final int stationId;

    public StationQueue(int stationId) {
        this.stationId = stationId;
        this.linkedList = new LinkedList<Measurement>();
    }

    public synchronized void Queue(Measurement measurement) throws InterruptedException {
        while(linkedList.size() > 30) {
            wait(0);
        }
        linkedList.addLast(measurement);
        this.notify();
    }

    public synchronized Measurement Dequeue() throws InterruptedException {
        while(linkedList.size() < 1) {
            wait(0);
        }
        Measurement measurement = linkedList.pollFirst();
        this.notify();
        return measurement;
    }

    public synchronized int getSize() { return linkedList.size(); }

}

/*
    T1 -> Append now 30
    T2 -> Append ?

 */