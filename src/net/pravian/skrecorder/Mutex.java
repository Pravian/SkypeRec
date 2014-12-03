package net.pravian.skrecorder;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.security.MessageDigest;
import java.util.logging.Level;

/**
 * The Class Mutex.
 *
 * @url http://nerdydevel.blogspot.com/2012/07/run-only-single-java-application-instance.html
 * @author rumatoest
 */
public class Mutex {

    private static Mutex instance;
    //
    private String key;
    private File lockFile;
    private FileLock lock;
    private FileChannel lockChannel;
    private FileOutputStream lockStream;

    private Mutex() {
    }

    private Mutex(String key) {
        String tmpDir = System.getProperty("java.io.tmpdir");
        if (!tmpDir.endsWith(System.getProperty("file.separator"))) {
            tmpDir += System.getProperty("file.separator");
        }

        // Aquire MD5
        try {
            final MessageDigest md = MessageDigest.getInstance("SHA1");
            md.reset();
            String hashText = new BigInteger(1, md.digest(key.getBytes())).toString(16);

            while (hashText.length() < 32) {
                hashText = "0" + hashText;
            }

            lockFile = new File(tmpDir + hashText + ".applock");
        } catch (Exception ex) {
            SkypeRec.LOGGER.severe("Failed to obtain mutex lock file!");
            throw new RuntimeException(ex);
        }

        if (lockFile == null) {
            lockFile = new File(tmpDir + key + ".applock");
        }
    }

    public boolean aquire() {
        try {
            lockStream = new FileOutputStream(lockFile);

            String fContent = "Java Mutex Object\r\nLocked by key: " + key + "\r\n";
            lockStream.write(fContent.getBytes());

            lockChannel = lockStream.getChannel();

            lock = lockChannel.tryLock();

            if (lock == null) {
                Util.handleError(new Exception("Could not create mutex lock"));
            }
        } catch (Exception ex) {
            return false;
        }

        return true;
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            release();
        } catch (Exception ignored) {
        }
        super.finalize();
    }

    private void release() throws Exception {
        if (lock.isValid()) {
            lock.release();
        }

        if (lockStream != null) {
            lockStream.close();
        }

        if (lockChannel.isOpen()) {
            lockChannel.close();
        }

        if (lockFile.exists()) {
            lockFile.delete();
        }
    }

    public static boolean aquireMutex(String key) {
        if (instance != null) {
            return false;
        }

        instance = new Mutex(key);

        if (!instance.aquire()) {
            return false;
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                Mutex.releaseMutex();
            }
        });
        return true;
    }

    public static void releaseMutex() {
        if (instance == null) {
            return;
        }
        try {
            instance.release();
        } catch (Throwable ex) {
            SkypeRec.LOGGER.log(Level.SEVERE, "Failed to release mutex lock!", ex);
        }
    }
}
