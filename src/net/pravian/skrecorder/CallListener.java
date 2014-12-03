package net.pravian.skrecorder;

import com.skype.Call;
import com.skype.CallMonitorListener;
import com.skype.SkypeException;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import net.pravian.skrecorder.audio.AudioMixer;

public class CallListener implements CallMonitorListener {

    private final CallRecorder listener;
    private final Map<String, SkypeCall> calls;
    private final SimpleDateFormat format;

    public CallListener(CallRecorder listener) {
        this.listener = listener;
        this.calls = new HashMap<>();
        this.format = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss");
    }

    @Override
    public void callMonitor(Call call, Call.Status status) throws SkypeException {
        if (listener.getSaveDir() == null) {
            return;
        }

        if (status == Call.Status.FINISHED) {
            callFinished(call);
        }

        if (status == Call.Status.INPROGRESS) {
            callStarted(call);
        }
    }

    private void callFinished(Call call) throws SkypeException {
        final String id = call.getId();

        SkypeRec.LOGGER.info("Call finished: " + call.getId());
        SkypeCall fCall = calls.remove(id);

        if (fCall == null) {
            // Finished call was not recorded
            SkypeRec.LOGGER.warning("Call was not recorded!");
            return;
        }

        AudioMixer.mixAudioFiles(fCall.getFiles(), getSaveFile(call));
    }

    private void callStarted(Call call) throws SkypeException {
        final String id = call.getId();

        if (calls.containsKey(id)) {
            return; // Already recording call
        }

        // Debuggles
        /*
         System.out.println("Call started with: " + call.getPartnerDisplayName());
         System.out.println("ConfId: " + call.getConferenceId());
         System.out.println("PartId: " + call.getPartnerId());
         System.out.println("Id: " + call.getId());
         System.out.println("PartCount: " + call.getParticipantsCount());
         System.out.println("PartDisName: " + call.getPartnerDisplayName());
         System.out.println("Part.DisName: " + call.getPartner().getDisplayName());
         System.out.println("Part.FuName: " + call.getPartner().getFullName());
         System.out.println("Part.Id: " + call.getPartner().getId());
         */

        final SkypeCall pCall = new SkypeCall(id);

        final File micFile = getTempSaveFile(call, "mic");
        call.setFileCaptureMic(micFile);
        pCall.addFile(micFile);

        final File outFile = getTempSaveFile(call, "out");
        call.setFileOutput(outFile);
        pCall.addFile(outFile);

        calls.put(id, pCall);

        SkypeRec.LOGGER.info("Call started: " + call.getId());
    }


    public File getTempSaveFile(Call call, String salt) throws SkypeException {
        return new File(listener.getTempDir(), getFileName(call, "." + salt + ".tmp"));
    }

    public File getSaveFile(Call call) throws SkypeException {
        return new File(listener.getSaveDir(), getFileName(call, ""));
    }

    private String getFileName(Call call, String salt) throws SkypeException {
        return format.format(new Date()) + "_ " + call.getId() + ".wav" + salt;
    }

}
