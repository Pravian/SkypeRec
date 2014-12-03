package net.pravian.skrecorder.logging;

import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.pravian.skrecorder.SkypeRec;

public class BasicLogger {

    private static Logger logger = null;
    private static Formatter formatter;

    public static Logger getLogger() {
        if (logger != null) {
            return logger;
        }

        formatter = new BasicFormatter();

        logger = Logger.getLogger(SkypeRec.class.getName());
        logger.setLevel(Level.INFO);
        logger.setUseParentHandlers(false);
        logger.addHandler(new BasicConsoleHandler(formatter));
        logger.addHandler(new BasicFileHandler(formatter, "latest.log"));

        logger.info("Initialized logger");
        return logger;
    }
}
