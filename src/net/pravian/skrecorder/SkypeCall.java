package net.pravian.skrecorder;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SkypeCall {

    private final Set<File> files;
    private final String id;

    public SkypeCall(String id) {
        this.id = id;
        this.files = new HashSet<>();
    }

    public String getId() {
        return id;
    }

    public void addFile(File file) {
        files.add(file);
    }

    public Set<File> getFiles() {
        return Collections.unmodifiableSet(files);
    }
}
