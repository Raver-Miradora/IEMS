package com.raver.iemsmaven.Model;

import java.time.LocalDateTime;

public class EmployeeDocument {
    private int documentId;
    private int userId;
    private String documentType;
    private String fileName;
    private byte[] fileData;
    private String googleDriveUrl;
    private LocalDateTime uploadedAt;

    public EmployeeDocument() {}

    public EmployeeDocument(int documentId, int userId, String documentType, String fileName, byte[] fileData, String googleDriveUrl, LocalDateTime uploadedAt) {
        this.documentId = documentId;
        this.userId = userId;
        this.documentType = documentType;
        this.fileName = fileName;
        this.fileData = fileData;
        this.googleDriveUrl = googleDriveUrl;
        this.uploadedAt = uploadedAt;
    }

    // Getters and Setters
    public int getDocumentId() { return documentId; }
    public void setDocumentId(int documentId) { this.documentId = documentId; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }
    
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    
    public byte[] getFileData() { return fileData; }
    public void setFileData(byte[] fileData) { this.fileData = fileData; }
    
    public String getGoogleDriveUrl() { return googleDriveUrl; }
    public void setGoogleDriveUrl(String googleDriveUrl) { this.googleDriveUrl = googleDriveUrl; }
    
    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
}
