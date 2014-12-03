package net.pravian.skrecorder;

import com.skype.SkypeException;
import java.io.File;
import java.util.logging.Logger;
import net.pravian.skrecorder.logging.BasicLogger;

public class SkypeRec {

    public static String NAME = "SkypeRec";
    public static final File APPFOLDER = Util.determineAppFolder();
    public static final Logger LOGGER = BasicLogger.getLogger();
    public static final SkypeRec APP = new SkypeRec();
    //
    private final CallRecorder recorder;

    public static void main(String[] args) throws SkypeException {
        try {
            if (!Mutex.aquireMutex(NAME)) {
                LOGGER.info("Another instance is already running; shutting down...");
                APP.stop(1);
            }

            APP.start();
        } finally {
            Mutex.releaseMutex();
        }
    }

    public SkypeRec() {
        this.recorder = new CallRecorder();
    }

    public void start() {
        recorder.start();
    }

    public void stop(int code) {
        LOGGER.info("Shutting down...");
        System.exit(code);
    }

    public CallRecorder getRecorder() {
        return recorder;
    }
}
