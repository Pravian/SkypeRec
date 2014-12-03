package net.pravian.skrecorder.audio;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import net.pravian.skrecorder.SkypeRec;
import static net.pravian.skrecorder.SkypeRec.LOGGER;
import net.pravian.skrecorder.Util;

public class AudioMixer {

    public static void mixAudioFiles(Collection<File> files, File output) {
        LOGGER.info("Mixing audio files");

        for (File file : files) {
            if (!file.exists()) {
                SkypeRec.LOGGER.severe("Could not mix audio files: One of the recordings unavailable: " + file.getAbsolutePath());
                return;
            }
        }

        final List<AudioInputStream> mixList = new ArrayList<>();
        AudioInputStream audioStream = null;

        try {
            AudioFormat audioFormat = null;
            for (File file : files) {
                AudioInputStream input;

                try {
                    input = AudioSystem.getAudioInputStream(file);
                } catch (Exception ex) {
                    Util.handleError(ex);
                    return;
                }

                if (audioFormat == null) {
                    audioFormat = input.getFormat();
                }

                mixList.add(input);
            }

            audioStream = new MixingAudioInputStream(audioFormat, mixList);

            LOGGER.info("Writing audio to file");
            AudioSystem.write(audioStream, Type.WAVE, output);
            LOGGER.info("Done.");
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Could not mix audio files!", ex);
        } finally {
            for (AudioInputStream stream : mixList) {
                try {
                    stream.close();
                } catch (Exception ex) {
                }
            }
            try {
                if (audioStream != null) {
                    audioStream.close();
                }
            } catch (Exception ex) {
            }
        }

    }
}