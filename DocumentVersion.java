package com.example;

import java.sql.Timestamp;

public class DocumentVersion {
    private int version;
    private String content;
    private String filename;
    private Timestamp createdAt;

    // Constructor
    public DocumentVersion(int version, String content, String filename, Timestamp createdAt) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Content cannot be null or empty");
        }
        if (filename == null || filename.trim().isEmpty()) {
            throw new IllegalArgumentException("Filename cannot be null or empty");
        }
        this.version = version;
        this.content = content;
        this.filename = filename;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Content cannot be null or empty");
        }
        this.content = content;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            throw new IllegalArgumentException("Filename cannot be null or empty");
        }
        this.filename = filename;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Version: " + version + "\n" +
                "Content: " + content + "\n" +
                "Filename: " + filename + "\n" +
                "Created At: " + createdAt;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        DocumentVersion that = (DocumentVersion) obj;
        return version == that.version &&
                content.equals(that.content) &&
                filename.equals(that.filename) &&
                createdAt.equals(that.createdAt);
    }

    @Override
    public int hashCode() {
        return 31 * version + content.hashCode() + filename.hashCode() + createdAt.hashCode();
    }
}