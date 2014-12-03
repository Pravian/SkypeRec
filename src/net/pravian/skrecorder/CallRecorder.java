package net.pravian.skrecorder;

import com.skype.Skype;
import com.skype.SkypeException;
import java.io.File;
import java.util.logging.Level;
import static net.pravian.skrecorder.SkypeRec.LOGGER;

public class CallRecorder {

    private final CallListener listener;
    private final File saveDir;
    private final File tempDir;

    public CallRecorder() {
        this.listener = new CallListener(this);
        this.saveDir = new File(SkypeRec.APPFOLDER, "saves");
        this.tempDir = new File(SkypeRec.APPFOLDER, "temp");
    }

    public void start() {

        setup();

        while (true) {
            main();
        }
    }

    private void setup() {
        LOGGER.info("Data directory: " + SkypeRec.APPFOLDER.getAbsolutePath());

        LOGGER.info("Enabling deamon mode");
        Skype.setDaemon(true); // to prevent exiting from this program

        Util.ensureExists(saveDir);
        Util.ensureExists(tempDir);

        for (File file : tempDir.listFiles()) {
            LOGGER.info("Deleting: " + file.getAbsolutePath());
            if (!file.delete()) {
                LOGGER.warning("Could not delete: " + file.getAbsolutePath());
                file.deleteOnExit();
            }
        }
    }

    private void main() {
        try {
            Thread.sleep(2000);
            if (!isSkypeRunning()) {
                LOGGER.info("Detected skype is not running; waiting...");

                while (!isSkypeRunning()) {
                    Thread.sleep(10000);
                }

                LOGGER.info("Detected skype running");
            }

            if (!addListeners()) {
                Util.handleError(new Exception("Could not attach listeners!")); // Exit
            }

            Util.showMessage("SkypeRec", "Successfully connected to Skype");
            LOGGER.info("Listening for calls");

            while (isSkypeRunning()) {
                Thread.sleep(10000);
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "An error occurred", ex);
        }
    }

    public boolean isSkypeRunning() {
        try {
            return Skype.isRunning();
        } catch (SkypeException ex) {
            return false;
        }
    }

    private boolean addListeners() {
        try {
            LOGGER.info("Adding listeners...");
            Skype.addCallMonitorListener(listener);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public File getSaveDir() {
        return saveDir;
    }

    public File getTempDir() {
        return tempDir;
    }

}
