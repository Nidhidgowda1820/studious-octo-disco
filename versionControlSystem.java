package com.example;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.security.MessageDigest;
import java.sql.*;
import java.util.Scanner;
import java.nio.charset.StandardCharsets;

public class versionControlSystem {

    private static final String URL = "jdbc:postgresql://localhost:5432/testdb";
    private static final String USER = "postgres";
    private static final String PASSWORD = "Nidhidgowdacse@2027";

    // Method to upload a document
    public void uploadDocument(String filePath) {
        String query = "INSERT INTO documents (version, content, filename, file_data, checksum, last_modified) VALUES (?, ?, ?, ?, ?, ?)";
        String checkQuery = "SELECT * FROM documents WHERE checksum = ?";

        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("The file does not exist.");
            return;
        }

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query);
             PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {

            byte[] fileData = Files.readAllBytes(file.toPath());
            String content = "File content placeholder"; // Placeholder, you can fetch actual content if needed
            String checksum = calculateChecksum(fileData);
            FileTime lastModifiedTime = Files.getLastModifiedTime(file.toPath());

            // Check if file with the same checksum already exists in the database
            checkStmt.setString(1, checksum);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                // File already exists, do not upload it
                System.out.println("File with the same checksum already exists in the database. Upload skipped.");
            } else {
                // File doesn't exist, proceed to upload
                pstmt.setInt(1, 1); // First version
                pstmt.setString(2, content);
                pstmt.setString(3, file.getName());
                pstmt.setBytes(4, fileData);
                pstmt.setString(5, checksum);
                pstmt.setTimestamp(6, new Timestamp(lastModifiedTime.toMillis()));

                pstmt.executeUpdate();
                System.out.println("File uploaded successfully!");
            }

        } catch (SQLException | IOException e) {
            System.err.println("Error while uploading the file.");
            e.printStackTrace();
        }
    }
    // Updated method to view document versions
    public void uploadDocument(String docName, String fileDirectory) {
        String insertQuery = "INSERT INTO documents (version, content, filename, file_data) VALUES (?, ?, ?, ?)";
        String selectQuery = "SELECT version, content FROM documents WHERE filename = ? ORDER BY version DESC LIMIT 1";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
             PreparedStatement selectStmt = conn.prepareStatement(selectQuery)) {

            // Check if document already exists in the database
            selectStmt.setString(1, docName);
            ResultSet rs = selectStmt.executeQuery();

            int newVersion = 1; // Default version is 1 when the document is uploaded for the first time
            String currentContent = null;

            if (rs.next()) {
                // Document already exists, compare content with the most recent version
                String lastContent = rs.getString("content");  // Last stored content
                File file = new File(fileDirectory + File.separator + docName);
                byte[] fileData = Files.readAllBytes(file.toPath());
                currentContent = new String(fileData, StandardCharsets.UTF_8); // Read current content

                // Compare content to detect changes
                if (!currentContent.equals(lastContent)) {
                    newVersion = rs.getInt("version") + 1;  // Increment version if changes are detected
                    // Insert the new version into the database
                    insertStmt.setInt(1, newVersion);
                    insertStmt.setString(2, currentContent);
                    insertStmt.setString(3, docName);
                    insertStmt.setBytes(4, fileData);
                    insertStmt.executeUpdate();
                    System.out.println("Changes detected! New version " + newVersion + " added.");
                } else {
                    System.out.println("No changes detected. Current version: " + rs.getInt("version"));
                }
            } else {
                // First upload, assign version 1
                File file = new File(fileDirectory + File.separator + docName);
                byte[] fileData = Files.readAllBytes(file.toPath());
                currentContent = new String(fileData, StandardCharsets.UTF_8); // Read current content

                // Insert the first version
                insertStmt.setInt(1, newVersion); // Version 1 for the first upload
                insertStmt.setString(2, currentContent);
                insertStmt.setString(3, docName);
                insertStmt.setBytes(4, fileData);
                insertStmt.executeUpdate();
                System.out.println("Document uploaded. Version 1 created.");
            }

        } catch (SQLException | IOException e) {
            System.err.println("Error while uploading document.");
            e.printStackTrace();
        }
    }

    public void viewDocumentVersions(String docName, String fileDirectory) {
        String selectQuery = "SELECT version, content, filename, checksum, last_modified FROM documents WHERE filename = ? ORDER BY version DESC";

        // Modified insert query with ON CONFLICT clause
        String insertQuery = "INSERT INTO documents (version, content, filename, file_data, checksum, last_modified) " +
                "VALUES (?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT (checksum) DO NOTHING";  // Skip insert if checksum already exists

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement selectStmt = conn.prepareStatement(selectQuery);
             PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {

            selectStmt.setString(1, docName);
            ResultSet rs = selectStmt.executeQuery();

            boolean found = false;
            int latestVersion = 0;
            String lastChecksum = null;
            Timestamp lastModifiedTimestamp = null;

            // Displaying existing versions
            System.out.println("Versions of document: " + docName);
            while (rs.next()) {
                found = true;
                int version = rs.getInt("version");
                latestVersion = version;
                lastChecksum = rs.getString("checksum");
                lastModifiedTimestamp = rs.getTimestamp("last_modified");
                String content = rs.getString("content");

                // Display all existing versions
                System.out.println("Version: " + version + ", Content: " + content);
            }

            if (!found) {
                System.out.println("No versions found for the document: " + docName);
            }

            // Construct full file path
            File file = new File(fileDirectory + File.separator + docName);
            if (file.exists()) {
                byte[] fileData = Files.readAllBytes(file.toPath());
                String currentChecksum = calculateChecksum(fileData); // Assuming this method calculates a checksum
                FileTime currentLastModified = Files.getLastModifiedTime(file.toPath());

                // Compare timestamps and checksums to detect changes
                if (!currentChecksum.equals(lastChecksum) ||
                        (lastModifiedTimestamp == null || currentLastModified.toMillis() > lastModifiedTimestamp.getTime())) {

                    // Increment version
                    int newVersion = latestVersion + 1;

                    insertStmt.setInt(1, newVersion);  // Incremented version
                    insertStmt.setString(2, "Updated content placeholder");  // Placeholder for actual content
                    insertStmt.setString(3, file.getName());
                    insertStmt.setBytes(4, fileData);  // Ensure file data is set here
                    insertStmt.setString(5, currentChecksum);
                    insertStmt.setTimestamp(6, new Timestamp(currentLastModified.toMillis()));

                    insertStmt.executeUpdate();
                    System.out.println("New version " + newVersion + " detected and added to the database!");
                } else {
                    // No changes detected, display the current version
                    System.out.println("No changes detected. Current version: " + latestVersion);
                }
            } else {
                System.out.println("File not found in the directory: " + fileDirectory);
                System.out.println("Only stored versions are displayed.");
            }

        } catch (SQLException | IOException e) {
            System.err.println("Error while retrieving or adding document versions.");
            e.printStackTrace();
        }
    }
    // Method to calculate checksum of file data
    private String calculateChecksum(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data);
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error calculating checksum", e);
        }
    }

    public static void main(String[] args) {
        versionControlSystem vcs = new versionControlSystem();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Choose an option:");
            System.out.println("1. Upload a document");
            System.out.println("2. View versions of a document");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            if (choice == 1) {
                System.out.print("Enter file path to upload: ");
                String filePath = scanner.nextLine();
                vcs.uploadDocument(filePath);
            } else if (choice == 2) {
                System.out.print("Enter the document name to view its versions: ");
                String docName = scanner.nextLine();
                System.out.print("Enter the directory where the document is located: ");
                String fileDirectory = scanner.nextLine();
                vcs.viewDocumentVersions(docName, fileDirectory);
            } else if (choice == 3) {
                System.out.println("Exiting the system.");
                break;
            } else {
                System.out.println("Invalid choice. Please try again.");
            }
        }

        scanner.close();
    }
}