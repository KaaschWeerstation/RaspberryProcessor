package nl.hanze.raspberryprocessor.Input;

import nl.hanze.raspberryprocessor.Data.Measurement;
import nl.hanze.raspberryprocessor.Data.MeasurementInputQueue;
import nl.hanze.raspberryprocessor.Main;
import nl.hanze.raspberryprocessor.Utility.ByteConversion;
import nl.hanze.raspberryprocessor.Utility.DecimalInt;
import nl.hanze.raspberryprocessor.Utility.SemaphoreInteger;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

public class ParallelInputSocket implements Runnable {

    private Socket connection;

    private MeasurementInputQueue measurementInputQueue;
    private BufferedInputStream inputStream;

    private Measurement measurement;
    private String measurementDateString;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-ddHH:mm:ss");
    private boolean trackElement = true;

    ParallelInputSocket(MeasurementInputQueue measurementInputQueue, Socket connection, SemaphoreInteger semaphoreInteger) throws IOException {
        this.measurementInputQueue = measurementInputQueue;
        this.connection = connection;
        this.inputStream = new BufferedInputStream(connection.getInputStream());
    }

    //private static byte[] bytes_MEA = {77, 69, 65};
    private static byte[] bytes_STN = {83, 84, 78};
    private static byte[] bytes_TIM = {84, 73, 77};
    private static byte[] bytes_DAT = {68, 65, 84};
    private static byte[] bytes_TEM = {84, 69, 77};
    private static byte[] bytes_DEW = {68, 69, 87};
    private static byte[] bytes_STP = {83, 84, 80};
    private static byte[] bytes_SLP = {83, 76, 80};
    private static byte[] bytes_VIS = {86, 73, 83};
    private static byte[] bytes_WDS = {87, 68, 83};
    private static byte[] bytes_PRC = {80, 82, 67};
    private static byte[] bytes_SND = {83, 78, 68};
    private static byte[] bytes_FRS = {70, 82, 83};
    private static byte[] bytes_CLD = {67, 76, 68};
    private static byte[] bytes_WND = {87, 78, 68};

    @Override
    public void run() {
        while(true) {
            try {
                // Assuming we are only receiving ascii
                byte aByte = (byte) inputStream.read();
                if (aByte == 60) { // <
                    //byte[] bytes = inputStream.readNBytes(3);  -- JAVA JDK 11+
                    byte[] bytes = {(byte) inputStream.read(), (byte) inputStream.read(), (byte) inputStream.read()};

                    // The first measurement we expect
                    if (Arrays.equals(bytes, bytes_STN)) {
                        measurement = new Measurement(); // Initialize measurement

                        inputStream.skip(1);
                        byte b;
                        byte[] chars = new byte[12];
                        int i = 0;

                        b = (byte) inputStream.read();
                        while (b != 60) {
                            chars[i] = b;
                            i++;
                            b = (byte) inputStream.read();
                        }

                        measurement.setStationId(Integer.parseInt(new String(chars).trim()));
                        trackElement = true;
                    }
                    else if (Arrays.equals(bytes, bytes_DAT)) {
                        inputStream.skip(2);
                        byte b;
                        byte[] chars = new byte[12];
                        int i = 0;

                        b = (byte) inputStream.read();
                        while (b != 60) {
                            chars[i] = b;
                            i++;
                            b = (byte) inputStream.read();
                        }
                        measurementDateString = new String(chars).trim();
                    }
                    else if (Arrays.equals(bytes, bytes_TIM)) {
                        inputStream.skip(2);
                        byte b;
                        byte[] chars = new byte[12];
                        int i = 0;

                        b = (byte) inputStream.read();
                        while (b != 60) {
                            chars[i] = b;
                            i++;
                            b = (byte) inputStream.read();
                        }

                        measurement.setLocalDateTime(LocalDateTime.parse((measurementDateString + new String(chars).trim()), formatter));
                        long t = measurement.getSecondOfDay();
                        if (Main.Settings.SelectSeconds) {
                            if (t % Main.Settings.SelectSecondsValue != 0) {
                                // Drop this measurement
                                trackElement = false;
                            }
                        }
                    }
                    else if (trackElement && Arrays.equals(bytes, bytes_TEM)) {
                        inputStream.skip(2);
                        byte b;
                        byte[] chars = new byte[12];
                        int i = 0;

                        b = (byte) inputStream.read();
                        while (b != 60) {
                            chars[i] = b;
                            i++;
                            b = (byte) inputStream.read();
                        }
                        try {
                            measurement.setTemperature(DecimalInt.parseDecimalInt(new String(chars).trim(), 2));
                        } catch (java.lang.NumberFormatException e) {
                            measurement.setTemperature(0);
                        }
                    }
                    else if (trackElement && Arrays.equals(bytes, bytes_DEW)) {
                        inputStream.skip(2);
                        byte b;
                        byte[] chars = new byte[12];
                        int i = 0;

                        b = (byte) inputStream.read();
                        while (b != 60) {
                            chars[i] = b;
                            i++;
                            b = (byte) inputStream.read();
                        }
                        try {
                            measurement.setDewPoint(DecimalInt.parseDecimalInt(new String(chars).trim(),2));
                        } catch (java.lang.NumberFormatException e) {
                            measurement.setDewPoint(0);
                        }
                    }
                    else if (trackElement && Arrays.equals(bytes, bytes_STP)) {
                        inputStream.skip(1);
                        byte b;
                        byte[] chars = new byte[12];
                        int i = 0;

                        b = (byte) inputStream.read();
                        while (b != 60) {
                            chars[i] = b;
                            i++;
                            b = (byte) inputStream.read();
                        }

                        measurement.setAirPressureStation(DecimalInt.parseDecimalInt(new String(chars).trim(),2));
                    }
                    else if (trackElement && Arrays.equals(bytes, bytes_SLP)) {
                        inputStream.skip(1);
                        byte b;
                        byte[] chars = new byte[12];
                        int i = 0;

                        b = (byte) inputStream.read();
                        while (b != 60) {
                            chars[i] = b;
                            i++;
                            b = (byte) inputStream.read();
                        }

                        try {
                            measurement.setAirPressureSeaLevel(DecimalInt.parseDecimalInt(new String(chars).trim(),2));
                        } catch (java.lang.NumberFormatException e) {
                            measurement.setAirPressureSeaLevel(0);
                        }
                    }
                    else if (trackElement && Arrays.equals(bytes, bytes_VIS)) {
                        inputStream.skip(3);
                        byte b;
                        byte[] chars = new byte[12];
                        int i = 0;

                        b = (byte) inputStream.read();
                        while (b != 60) {
                            chars[i] = b;
                            i++;
                            b = (byte) inputStream.read();
                        }
                        measurement.setVisibility(DecimalInt.parseDecimalInt(new String(chars).trim(),2));
                    }
                    else if (trackElement && Arrays.equals(bytes, bytes_WDS)) {
                        inputStream.skip(2);
                        byte b;
                        byte[] chars = new byte[12];
                        int i = 0;

                        b = (byte) inputStream.read();
                        while (b != 60) {
                            chars[i] = b;
                            i++;
                            b = (byte) inputStream.read();
                        }
                        try {
                            measurement.setWindSpeed(DecimalInt.parseDecimalInt(new String(chars).trim(),2));
                        } catch (java.lang.NumberFormatException e) {
                            measurement.setWindSpeed(0);
                        }
                    }
                    else if (trackElement && Arrays.equals(bytes, bytes_PRC)) {
                        inputStream.skip(2);
                        byte b;
                        byte[] chars = new byte[12];
                        int i = 0;

                        b = (byte) inputStream.read();
                        while (b != 60) {
                            chars[i] = b;
                            i++;
                            b = (byte) inputStream.read();
                        }

                        measurement.setPrecipitation(DecimalInt.parseDecimalInt(new String(chars).trim(),2));
                    }
                    else if (trackElement && Arrays.equals(bytes, bytes_SND)) {
                        inputStream.skip(2);
                        byte b;
                        byte[] chars = new byte[12];
                        int i = 0;

                        b = (byte) inputStream.read();
                        while (b != 60) {
                            chars[i] = b;
                            i++;
                            b = (byte) inputStream.read();
                        }
                        try {
                            measurement.setFallenSnow(DecimalInt.parseDecimalInt( new String(chars).trim(),2));
                        } catch (java.lang.NumberFormatException e) {
                            measurement.setFallenSnow(0);
                        }
                    }
                    else if (trackElement && Arrays.equals(bytes, bytes_FRS)) {
                        inputStream.skip(4);
                        byte b;
                        byte[] chars = new byte[12];
                        int i = 0;

                        b = (byte) inputStream.read();
                        while (b != 60) {
                            chars[i] = b;
                            i++;
                            b = (byte) inputStream.read();
                        }
                        try {
                            measurement.setEvents(Byte.valueOf(new String(chars).trim(), 2));
                        } catch (java.lang.NumberFormatException e) {
                            measurement.setEvents((byte) 0);
                        }
                    }
                    else if (trackElement && Arrays.equals(bytes, bytes_CLD)) {
                        inputStream.skip(2);
                        byte b;
                        byte[] chars = new byte[12];
                        int i = 0;

                        b = (byte) inputStream.read();
                        while (b != 60) {
                            chars[i] = b;
                            i++;
                            b = (byte) inputStream.read();
                        }
                        try {
                            measurement.setCloudCoverage(DecimalInt.parseDecimalInt(new String(chars).trim(),2));
                        } catch (java.lang.NumberFormatException e) {
                            measurement.setCloudCoverage(0);
                        }
                    }
                    // Last measurement we expect
                    else if (trackElement && Arrays.equals(bytes, bytes_WND)) {
                        inputStream.skip(4);
                        byte b;
                        byte[] chars = new byte[12];
                        int i = 0;

                        b = (byte) inputStream.read();
                        while (b != 60) {
                            chars[i] = b;
                            i++;
                            b = (byte) inputStream.read();
                        }
                        try {
                            measurement.setWindDirection(DecimalInt.parseDecimalInt(new String(chars).trim(),2));
                        } catch (java.lang.NumberFormatException e) {
                            measurement.setWindDirection(0);
                        }
                        measurementInputQueue.add(measurement);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

}

