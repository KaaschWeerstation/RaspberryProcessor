package nl.hanze.raspberryprocessor.Input;

import nl.hanze.raspberryprocessor.Data.Measurement;
import nl.hanze.raspberryprocessor.Data.MeasurementInputQueue;
import nl.hanze.raspberryprocessor.Main;
import nl.hanze.raspberryprocessor.Utility.SemaphoreInteger;
import nl.hanze.raspberryprocessor.Utility.DecimalInt;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.stax.StAXSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.time.temporal.ChronoField.SECOND_OF_DAY;

public class ParallelInputSocket implements Runnable {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-ddHH:mm:ss");
    private static final XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
    private static final SchemaFactory  schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

    private MeasurementInputQueue measurementInputQueue;
    //private XMLInputFactory xmlInputFactory;
    private XMLEventReader xmlEventReader;
    private BufferedReader bufferedReader;
    //private SchemaFactory schemaFactory;
    private Schema schema;
    private Validator validator;

    private StringReader stringReader;
    private String readString;
    private StringBuilder stringBuilder;

    private Socket connection;

    private Measurement measurement;
    private String measurementDateString;
    private String currentXMLElement;

    private SemaphoreInteger semaphoreInteger;

    ParallelInputSocket(MeasurementInputQueue measurementInputQueue, Socket connection, SemaphoreInteger semaphoreInteger) {
        this.measurementInputQueue = measurementInputQueue;
        //xmlInputFactory = XMLInputFactory.newInstance();
        this.connection = connection;

        //schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            //xmlEventReader = xmlInputFactory.createXMLEventReader(bufferedReader);

            schema = schemaFactory.newSchema(new File("src/nl/hanze/raspberryprocessor/Resources/example.xsd"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        validator = schema.newValidator();
        validator.setErrorHandler(new EmptyErrorHandler());

        stringBuilder = new StringBuilder();

        this.semaphoreInteger = semaphoreInteger;

//        xmlEventReader = new EventReaderDelegate(xmlEventReader) {
//            public XMLEvent nextEvent() throws XMLStreamException {
//                XMLEvent event = super.nextEvent();
//
//                handleEvent(event);
//
//                return event;
//            }
//        };
    }

    // Split each "Burst" of data on <MEASUREMENT>
    //
    // Then pump it into a StringReader
    // Validate that
    // If valid then actually process it.

    @Override
    public void run() {
        while(!connection.isClosed()) {
            try {
                // Read the Socket bufferedReader (Input)
                readString = bufferedReader.readLine();

                if (readString != null) {
                    readString = readString.trim(); // Trim unwanted spaces/tabs

                    // Are we at the start of a XML data blob?
                    if (readString.equals("<MEASUREMENT>")) {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append(readString);

                    // Are we at the end of a XML data blob?
                    } else if (readString.equals("</MEASUREMENT>")) {
                        stringBuilder.append(readString);

                        String result = stringBuilder.toString();
                        stringReader = new StringReader(result);
                        xmlEventReader = xmlInputFactory.createXMLEventReader(stringReader);

                        while (xmlEventReader.hasNext()) {
                            handleEvent(xmlEventReader.nextEvent());
                        }
//                        String result = stringBuilder.toString(); // Turn the result into a string
//                        if (isXMLValid(result)) {
//                            try {
//                                stringReader = new StringReader(result);
//                                xmlEventReader = xmlInputFactory.createXMLEventReader(stringReader);
//
//                                while (xmlEventReader.hasNext()) {
//                                    handleEvent(xmlEventReader.nextEvent());
//                                }
//
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
                    // Somewhere in the middle, just append.
                    } else {
                        stringBuilder.append(readString);
                    }
                } else {
                    connection.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Connection is closed
        System.out.println("Connection is closed");
        try {
            semaphoreInteger.decrement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isXMLValid(String xml) {

        stringReader = new StringReader(xml);
        try {
            xmlEventReader = xmlInputFactory.createXMLEventReader(stringReader);
            //validator.validate((new StAXSource(xmlEventReader)));
        } catch (Exception e) {
            //System.out.println("INVALID XML");
            return false;
            //e.printStackTrace();
        }

        //System.out.println("VALID XML");
        return true;
    }

    private boolean trackElement;
    private void handleEvent(XMLEvent nextEvent) {
        if (nextEvent.isStartElement()) {
            StartElement startElement = nextEvent.asStartElement();
            currentXMLElement = startElement.getName().getLocalPart();

            if(currentXMLElement.equals("MEASUREMENT")) {
                trackElement = true;
                measurement = new Measurement();
            }
        } else if (nextEvent.isEndElement()) {
            EndElement endElement = nextEvent.asEndElement();
            currentXMLElement = endElement.getName().getLocalPart();

            if(currentXMLElement.equals("MEASUREMENT")) {
                if (trackElement) {
                    measurementInputQueue.add(measurement);
                   // measurement.verifyValues();
                }
            }

        } else {
            if(currentXMLElement != null && !nextEvent.toString().trim().equals("") && trackElement) {
                switch (currentXMLElement) {
                    case "STN":
                        measurement.setStationId(Integer.parseInt(nextEvent.toString()));
                        break;
                    case "DATE":
                        measurementDateString = nextEvent.toString();
                        break;
                    case "TIME":
                        measurement.setLocalDateTime(LocalDateTime.parse((measurementDateString + nextEvent.toString()), formatter));
                        long t = measurement.getSecondOfDay();
                        if (Main.Settings.SelectSeconds) {
                            if (t % Main.Settings.SelectSecondsValue != 0) {
                                // Drop this measurement
                                trackElement = false;
                            }
                        }
                        break;
                    case "TEMP":
                        measurement.setTemperature(DecimalInt.parseDecimalInt(nextEvent.toString(), 2));
                        break;
                    case "DEWP":
                        measurement.setDewPoint(DecimalInt.parseDecimalInt(nextEvent.toString(),2));
                        break;
                    case "STP":
                        measurement.setAirPressureStation(DecimalInt.parseDecimalInt(nextEvent.toString(),2));
                        break;
                    case "SLP":
                        measurement.setAirPressureSeaLevel(DecimalInt.parseDecimalInt(nextEvent.toString(),2));
                        break;
                    case "VISIB":
                        measurement.setVisibility(DecimalInt.parseDecimalInt(nextEvent.toString(),2));
                        break;
                    case "WDSP":
                        measurement.setWindSpeed(DecimalInt.parseDecimalInt(nextEvent.toString(),2));
                        break;
                    case "PRCP":
                        measurement.setPrecipitation(DecimalInt.parseDecimalInt(nextEvent.toString(),2));
                        break;
                    case "SNDP":
                        measurement.setFallenSnow(DecimalInt.parseDecimalInt(nextEvent.toString(),2));
                        break;
                    case "FRSHTT":
                        measurement.setEvents(Byte.valueOf(nextEvent.toString(), 2));
                        break;
                    case "CLDC":
                        measurement.setCloudCoverage(DecimalInt.parseDecimalInt(nextEvent.toString(),2));
                        break;
                    case "WNDDIR":
                        measurement.setWindDirection(DecimalInt.parseDecimalInt(nextEvent.toString(),2));
                        break;
                    default:
                        break;
                }
            }
        }
    }

    class EmptyErrorHandler implements ErrorHandler {
        @Override
        public void fatalError( SAXParseException e ) throws SAXException {
            throw e;
        }
        @Override
        public void error( SAXParseException e ) throws SAXException {
            throw e;
        }
        @Override
        public void warning( SAXParseException e ) throws SAXException {
            // noop
        }
    }
}

//class Test {
//
//    Socket connection;
//    MeasurementInputQueue measurementInputQueue;
//
//    Test() {
//        try {
//            measurementInputQueue = new MeasurementInputQueue();
//            ServerSocket serverSocket = new ServerSocket(7789);
//            connection = serverSocket.accept();
//            connection.setSoTimeout(5000);
//            ParallelInputSocket parallelInputSocket = new ParallelInputSocket(measurementInputQueue, connection);
//            parallelInputSocket.run();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}

