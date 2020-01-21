package nl.hanze.raspberryprocessor.Data;

import nl.hanze.raspberryprocessor.Main;
import nl.hanze.raspberryprocessor.Utility.ByteConversion;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;

import static java.time.temporal.ChronoField.SECOND_OF_DAY;

public class Measurement {
    private static final long serialVersionUID= Main.serialVersionUID;

    private int stationId;
    private LocalDateTime localDateTime;
    private int temperature;
    private int dewPoint;
    private int airPressureSeaLevel;
    private int visibility;
    private int airPressureStation;
    private int windSpeed;
    private int precipitation;
    private int fallenSnow;
    private byte events;
    private int cloudCoverage;
    private int windDirection;

    public int getStationId() {
        return stationId;
    }
    public void setStationId(int stationId) {
        this.stationId = stationId;
    }

    public int getTemperature() {
        return temperature;
    }
    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public int getDewPoint() {
        return dewPoint;
    }
    public void setDewPoint(int dewPoint) {
        this.dewPoint = dewPoint;
    }

    public int getAirPressureSeaLevel() {
        return airPressureSeaLevel;
    }
    public void setAirPressureSeaLevel(int airPressureSeaLevel) {
        this.airPressureSeaLevel = airPressureSeaLevel;
    }

    public int getVisibility() {
        return visibility;
    }
    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }

    public int getAirPressureStation() {
        return airPressureStation;
    }
    public void setAirPressureStation(int airPressureStation) {
        this.airPressureStation = airPressureStation;
    }

    public int getWindSpeed() {
        return windSpeed;
    }
    public void setWindSpeed(int windSpeed) {
        this.windSpeed = windSpeed;
    }

    public int getPrecipitation() {
        return precipitation;
    }
    public void setPrecipitation(int precipitation) {
        this.precipitation = precipitation;
    }

    public int getFallenSnow() {
        return fallenSnow;
    }
    public void setFallenSnow(int fallenSnow) {
        this.fallenSnow = fallenSnow;
    }

    public byte getEvents() {
        return events;
    }
    public void setEvents(byte events) {
        this.events = events;
    }

    public int getCloudCoverage() {
        return cloudCoverage;
    }
    public void setCloudCoverage(int cloudCoverage) {
        this.cloudCoverage = cloudCoverage;
    }

    public int getWindDirection() {
        return windDirection;
    }
    public void setWindDirection(int windDirection) {
        this.windDirection = windDirection;
    }

    public Measurement() {}

    public String toString() {

        return String.format("stationId: %d\ndatetime: %s\ntemperature: %f\ndewPoint: %f\nairPressureStation: %f"
                        + "\nairPressureSeaLevel: %f\nvisibility: %f\nwindSpeed: %f\nprecipitation: %f\nfallenSnow: %f\nevents: %s"
                        + "\ncloudCoverage %f\nwindDirection: %d", stationId, localDateTime, temperature, dewPoint, airPressureStation,
                airPressureSeaLevel, visibility, windSpeed, precipitation, fallenSnow, events, cloudCoverage, windDirection
        );

    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }
    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }
    
    public void Save(FileOutputStream fileOutputStream, ByteConversion byteConversion) throws IOException {
        System.out.println("Id: " + Integer.toString(stationId));
        System.out.println(temperature);
        System.out.println(Arrays.toString(byteConversion.intToBytes(temperature)));
        fileOutputStream.write(byteConversion.intToBytes(temperature));
        fileOutputStream.write(byteConversion.intToBytes(dewPoint));
        fileOutputStream.write(byteConversion.intToBytes(airPressureSeaLevel));
        fileOutputStream.write(byteConversion.intToBytes(visibility));
        fileOutputStream.write(byteConversion.intToBytes(airPressureStation));
        fileOutputStream.write(byteConversion.intToBytes(windSpeed));
        fileOutputStream.write(byteConversion.intToBytes(precipitation));
        fileOutputStream.write(byteConversion.intToBytes(fallenSnow));
        fileOutputStream.write(byteConversion.intToBytes(events));
        fileOutputStream.write(byteConversion.intToBytes(cloudCoverage));
        fileOutputStream.write(byteConversion.intToBytes(windDirection));
        //byte[] epoch_day = ByteConversion.longToBytes(localDateTime.getLong(EPOCH_DAY));
        byte[] second_of_day = byteConversion.longToBytes(localDateTime.getLong(SECOND_OF_DAY));
        //fileOutputStream.write(epoch_day);
        fileOutputStream.write(second_of_day);
    }
}


