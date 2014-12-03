package net.pravian.skrecorder.logging;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import net.pravian.skrecorder.SkypeRec;
import net.pravian.skrecorder.Util;

public class BasicFileHandler extends Handler {

    private final File outputFile;
    private final Formatter formatter;
    private final Handler handle;

    public BasicFileHandler(Formatter formatter, String file) {
        this.outputFile = new File(SkypeRec.APPFOLDER, file);
        this.formatter = formatter;


        Handler tempHandler;
        try {
            tempHandler = new FileHandler(outputFile.getAbsolutePath());
            tempHandler.setFormatter(formatter);
        } catch (IOException ex) {
            Util.handleError(ex);
            ex.printStackTrace();
            tempHandler = null;
        }
        this.handle = tempHandler;
    }

    @Override
    public void publish(LogRecord lr) {
        handle.publish(lr);
    }

    @Override
    public void flush() {
        handle.flush();
    }

    @Override
    public void close() throws SecurityException {
        handle.close();
    }

}
