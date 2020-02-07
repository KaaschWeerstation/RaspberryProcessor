package nl.hanze.raspberryprocessor.Input;

import nl.hanze.raspberryprocessor.Data.Measurement;
import nl.hanze.raspberryprocessor.Data.MeasurementInputQueue;
import nl.hanze.raspberryprocessor.Main;
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

public class ParallelInputSocket implements Runnable {

    private Socket connection;

    private MeasurementInputQueue measurementInputQueue;

    private BufferedInputStream inputStream;

    ParallelInputSocket(MeasurementInputQueue measurementInputQueue, Socket connection, SemaphoreInteger semaphoreInteger) throws IOException {
        this.measurementInputQueue = measurementInputQueue;
        this.connection = connection;
        this.inputStream = new BufferedInputStream(connection.getInputStream());
    }

    private static byte[] bytes_MEA = {77, 69, 65};
    private static byte[] bytes_STN = {83, 84, 78};
    private static byte[] bytes_TIM = {84, 73, 77};
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

                if (aByte == 60) {
                    byte[] bytes = inputStream.readNBytes(3);

                    // The first measurement we expect
                    if (bytes == bytes_STN) {
                        inputStream.skipNBytes(1);



                    } else if (bytes == bytes_TIM) {

                    } else if (bytes == bytes_TEM) {

                    } else if (bytes == bytes_DEW) {

                    } else if (bytes == bytes_STP) {

                    } else if (bytes == bytes_SLP) {

                    } else if (bytes == bytes_VIS) {

                    } else if (bytes == bytes_WDS) {

                    } else if (bytes == bytes_PRC) {

                    } else if (bytes == bytes_SND) {

                    } else if (bytes == bytes_FRS) {

                    } else if (bytes == bytes_CLD) {

                    } else if (bytes == bytes_WND) {

                    }
                }


            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

    private int readInt() {

    }
}

