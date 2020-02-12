package nl.hanze.raspberryprocessor.Data;

import nl.hanze.raspberryprocessor.Main;

import java.util.LinkedList;
import java.util.function.IntConsumer;

import static java.lang.Math.round;

/**
 * A Wrapper around a LinkedList meant to keep x measurements of a station in buffer.
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
        while(linkedList.size() > Main.Settings.QueueSize) {
            wait(0);
        }

        verify(measurement);

        // TODO: Possibly make this optional - Check for missing measurement as a whole
//        if (linkedList.size() > 0) {
//            Measurement previousMeasurement = linkedList.peekLast();
//            long diff = measurement.getSecondOfDay() - previousMeasurement.getSecondOfDay();
//            System.out.println(diff);
//            if (diff > 1) {
//                System.out.println("Missing Value???");
//            }
//        }

        linkedList.addLast(measurement);
        this.notifyAll();
    }

    public synchronized Measurement Dequeue() throws InterruptedException {
        while(linkedList.size() < Main.Settings.QueueSize) {
            wait(0);
        }
        Measurement measurement = linkedList.pollFirst();
        this.notifyAll();
        return measurement;
    }

    public synchronized int getSize() { return linkedList.size(); }

    enum ValueType {
        TEMPERATURE,
        DEWPOINT,
        PRESSURE_SEA_LEVEL,
        PRESSURE_STATION,
        VISIBILITY,
        WIND_SPEED,
        WIND_DIRECTION,
        CLOUDAVG

    }

    private void verify(Measurement measurement) {
        if (measurement.getTemperature() == 0) {
            measurement.setTemperature(getExponentialMovingAverage(ValueType.TEMPERATURE));
            //System.out.println("Missing temp: " + getExponentialMovingAverage(ValueType.TEMPERATURE) + " old: " + measurement.getTemperature());
        }
        if (measurement.getDewPoint() == 0) {
            measurement.setDewPoint(getExponentialMovingAverage(ValueType.DEWPOINT));
            //System.out.println("Missing dew: " + getExponentialMovingAverage(ValueType.DEWPOINT) + " old: " + measurement.getDewPoint());
        }
        if (measurement.getAirPressureSeaLevel() == 0) {
            measurement.setAirPressureSeaLevel(getExponentialMovingAverage(ValueType.PRESSURE_SEA_LEVEL));
            //System.out.println("Missing sea: " + getExponentialMovingAverage(ValueType.PRESSURE_SEA_LEVEL) + " old: " + measurement.getAirPressureSeaLevel());
        }
        if (measurement.getAirPressureStation() == 0) {
            measurement.setAirPressureStation(getExponentialMovingAverage(ValueType.PRESSURE_STATION));
            //System.out.println("Missing stat: " + getExponentialMovingAverage(ValueType.PRESSURE_STATION) + " old: " + measurement.getAirPressureStation());
        }
        if (measurement.getVisibility() == 0) {
            measurement.setVisibility(getExponentialMovingAverage(ValueType.VISIBILITY));
            //System.out.println("Missing vis: " + getExponentialMovingAverage(ValueType.VISIBILITY) + " old: " + measurement.getVisibility());
        }
        if (measurement.getWindSpeed() == 0) {
            measurement.setWindSpeed(getExponentialMovingAverage(ValueType.WIND_SPEED));
            //System.out.println("Missing winds: " + getExponentialMovingAverage(ValueType.WIND_SPEED) + " old: " + measurement.getWindSpeed());
        }
        if (measurement.getWindDirection() == 0) {
            measurement.setWindDirection(getExponentialMovingAverage(ValueType.WIND_DIRECTION));
            //System.out.println("Missing windd: " + getExponentialMovingAverage(ValueType.WIND_DIRECTION) + " old: " + measurement.getWindDirection());
        }
        if (measurement.getCloudCoverage() == 0) {
            measurement.setCloudCoverage(getExponentialMovingAverage(ValueType.CLOUDAVG));
            //System.out.println("Missing cloud: " + getExponentialMovingAverage(ValueType.CLOUDAVG) + " old: " + measurement.getCloudCoverage());
        }
    }

    private int getExponentialMovingAverage(ValueType value) {
        float alpha = .5f;
        int average = 0;

        for (int i = 0; i < linkedList.size(); i++) {
            Measurement m = linkedList.get(i);
            if (i == 0) {
                average = getValue(value, m);
            } else {
                int val = getValue(value, m);
                average = round((1-alpha)*average+alpha*val);
            }
        }
        return average;
    }

    private int getValue(ValueType value, Measurement measurement) {
        switch (value) {
            case TEMPERATURE:
                return measurement.getTemperature();
            case DEWPOINT:
                return measurement.getDewPoint();
            case PRESSURE_SEA_LEVEL:
                return measurement.getAirPressureSeaLevel();
            case PRESSURE_STATION:
                return measurement.getAirPressureStation();
            case VISIBILITY:
                return measurement.getVisibility();
            case WIND_SPEED:
                return measurement.getWindSpeed();
            case WIND_DIRECTION:
                return measurement.getWindDirection();
            case CLOUDAVG:
                return measurement.getCloudCoverage();
            default:
                return 0;
        }
    }




}
