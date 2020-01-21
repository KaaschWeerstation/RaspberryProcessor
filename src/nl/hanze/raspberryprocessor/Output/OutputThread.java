package nl.hanze.raspberryprocessor.Output;

import nl.hanze.raspberryprocessor.Data.Measurement;
import nl.hanze.raspberryprocessor.Data.StationQueue;
import nl.hanze.raspberryprocessor.Main;
import nl.hanze.raspberryprocessor.Utility.ByteConversion;

import java.io.File;
import java.io.FileOutputStream;

import static java.time.temporal.ChronoField.EPOCH_DAY;

public class OutputThread implements Runnable {
    private final byte[] serialVersionUID;
    private ByteConversion byteConversion;

    private final File destinationDirectory;
    private final int stationId;
    private StationQueue stationQueue;
    private long currentDate;
    private FileOutputStream currentFileOutputStream;

    public OutputThread(File parentDestinationDirectory, int stationId, StationQueue stationQueue) {
        byteConversion = new ByteConversion();

        serialVersionUID = byteConversion.longToBytes(Main.serialVersionUID);


        this.stationId = stationId;
        this.stationQueue = stationQueue;
        this.destinationDirectory = new File(parentDestinationDirectory.getPath() + "/" + stationId);
        if (!destinationDirectory.exists()) { destinationDirectory.mkdir(); }
    }

    @Override
    public void run() {
        while(true) {
            try {
                Measurement measurement = stationQueue.Dequeue();
                long date = measurement.getLocalDateTime().getLong(EPOCH_DAY);
                if (currentDate == date) {
                    try {
                        measurement.Save(currentFileOutputStream, byteConversion);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    currentDate = date;
                    if (currentFileOutputStream != null) {
                        try {
                            currentFileOutputStream.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    File currentFile = new File(destinationDirectory + "/" + date + ".wd");
                    try {
                        currentFile.createNewFile();
                        currentFileOutputStream = new FileOutputStream(currentFile, true);
                        writeHeader(currentFileOutputStream);
                        measurement.Save(currentFileOutputStream, byteConversion);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {

            }
        }
    }

    private void writeHeader(FileOutputStream fileOutputStream) {
        try {
            fileOutputStream.write(serialVersionUID);
            fileOutputStream.write(byteConversion.intToBytes(stationId));
            fileOutputStream.write(byteConversion.longToBytes(currentDate));
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}

/*
    1. Get first date
    2. See if file exists
        No: -> Create file
            -> Add Header
        No/Yes: -> Write Measurements till a new date is found

    3. Get new date:
        -> Close File
        -> Step 2
 */