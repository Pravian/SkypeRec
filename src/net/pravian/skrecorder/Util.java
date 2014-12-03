package net.pravian.skrecorder;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.swing.JOptionPane;

public class Util {

    public static void handleError(Throwable ex) {
        final String stacktrace = getStackTrace(ex);
        SkypeRec.LOGGER.severe(stacktrace);
        JOptionPane.showMessageDialog(null,
                "SkypeRec:\n"
                + stacktrace,
                "An error occured",
                JOptionPane.ERROR_MESSAGE);
        SkypeRec.APP.stop(1);
    }

    public static void showMessage(String title, String message) {
        JOptionPane.showMessageDialog(null,
                message,
                title,
                JOptionPane.INFORMATION_MESSAGE);
    }

    private static String getStackTrace(Throwable throwable) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }

    public static void ensureExists(File folder) {
        if (!folder.exists()) {
            SkypeRec.LOGGER.info("Creating folder: " + folder.getAbsolutePath());
            folder.mkdirs();
        }
    }

    public static File determineAppFolder() {

        final String os = System.getProperty("os.name").toUpperCase();
        File parentFolder = null;

        // Windows
        if (os.contains("WIN")) {
            final String appData = System.getenv("APPDATA");

            if (appData != null && !appData.isEmpty()) {
                final File appDataFolder = new File(appData);
                if (appDataFolder.exists()) {
                    parentFolder = appDataFolder;
                }
            }
        }

        // Linux/Mac
        if (parentFolder == null) {
            final String userHome = System.getProperty("user.home");

            if (userHome != null && !userHome.isEmpty()) {
                final File userHomeFolder = new File(userHome);
                if (userHomeFolder.exists()) {
                    parentFolder = userHomeFolder;
                }
            }
        }

        // Last resort: Current directory
        if (parentFolder == null) {
            parentFolder = new File(".");
        }

        final File dataFolder = new File(parentFolder, SkypeRec.NAME);
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        return dataFolder;
    }
}
