package com.raver.iemsmaven.Model;

import java.time.LocalDateTime;
import javafx.beans.property.*;

public class BackupFile {
    private final StringProperty fileName;
    private final ObjectProperty<LocalDateTime> dateCreated;
    private final StringProperty fileSize;
    private final String fullPath;

    public BackupFile(String fileName, LocalDateTime dateCreated, String fileSize, String fullPath) {
        this.fileName = new SimpleStringProperty(fileName);
        this.dateCreated = new SimpleObjectProperty<>(dateCreated);
        this.fileSize = new SimpleStringProperty(fileSize);
        this.fullPath = fullPath;
    }

    public String getFileName() { return fileName.get(); }
    public StringProperty fileNameProperty() { return fileName; }
    
    public LocalDateTime getDateCreated() { return dateCreated.get(); }
    public ObjectProperty<LocalDateTime> dateCreatedProperty() { return dateCreated; }
    
    public String getFileSize() { return fileSize.get(); }
    public StringProperty fileSizeProperty() { return fileSize; }
    
    public String getFullPath() { return fullPath; }
}
