package com.example;

import java.util.Scanner;


public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        versionControlSystem versionControlSystem = new versionControlSystem(); // Correct initialization

        while (true) {
            System.out.println("Choose an option:");
            System.out.println("1. Upload a document");
            System.out.println("2. View versions of a document");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline

            switch (choice) {
                case 1:
                    // Prompt user to enter the document file path
                    System.out.print("Enter the full path to the document you want to upload: ");
                    String filePath = scanner.nextLine();
                    versionControlSystem.uploadDocument(filePath);  // Correctly passing the file path
                    break;

                case 2:
                    // Prompt user to enter the document name to view its versions
                    System.out.print("Enter the document name to view its versions: ");
                    String viewDocName = scanner.nextLine();

                    // Prompt user to enter the directory where the document is located
                    System.out.print("Enter the directory where the document is located: ");
                    String fileDirectory = scanner.nextLine();

                    // Pass both the document name and directory to the method
                    versionControlSystem.viewDocumentVersions(viewDocName, fileDirectory);
                    break;


                case 3:
                    // Exit the program
                    System.out.println("Exiting the program.");
                    scanner.close();
                    System.exit(0);
                    break;

                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
}
