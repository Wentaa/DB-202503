package edu.uob;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.nio.file.Files;

/** This class implements the DB server. */
public class DBServer {

    private static final char END_OF_TRANSMISSION = 4;
    private String storageFolderPath;
    private String currentDatabase;

    public static void main(String args[]) throws IOException {
        DBServer server = new DBServer();
        server.blockingListenOn(8888);
    }

    /**
     * KEEP this signature otherwise we won't be able to mark your submission correctly.
     */
    public DBServer() {
        storageFolderPath = Paths.get("databases").toAbsolutePath().toString();
        try {
            // Create the database storage folder if it doesn't already exist !
            Files.createDirectories(Paths.get(storageFolderPath));
        } catch(IOException ioe) {
            System.out.println("Can't seem to create database storage folder " + storageFolderPath);
        }
    }

    /**
     * KEEP this signature (i.e. {@code edu.uob.DBServer.handleCommand(String)}) otherwise we won't be
     * able to mark your submission correctly.
     *
     * <p>This method handles all incoming DB commands and carries out the required actions.
     */
    public String handleCommand(String command) {
        try {
            // Command preprocessing - trim and normalize whitespace
            command = command.trim();

            // Parse and execute command
            if (command.toUpperCase().startsWith("USE")) {
                return handleUseCommand(command);
            } else if (command.toUpperCase().startsWith("CREATE")) {
                return handleCreateCommand(command);
            } else if (command.toUpperCase().startsWith("INSERT")) {
                return handleInsertCommand(command);
            } else if (command.toUpperCase().startsWith("SELECT")) {
                return handleSelectCommand(command);
            } else if (command.toUpperCase().startsWith("UPDATE")) {
                return handleUpdateCommand(command);
            } else if (command.toUpperCase().startsWith("DELETE")) {
                return handleDeleteCommand(command);
            } else if (command.toUpperCase().startsWith("DROP")) {
                return handleDropCommand(command);
            } else if (command.toUpperCase().startsWith("ALTER")) {
                return handleAlterCommand(command);
            } else if (command.toUpperCase().startsWith("JOIN")) {
                return handleJoinCommand(command);
            } else {
                return "[ERROR] Unrecognized command";
            }
        } catch (Exception e) {
            // Ensure server never crashes
            return "[ERROR] " + e.getMessage();
        }
    }

    // Command handler methods
    private String handleUseCommand(String command) {
        // Implementation for USE database command
        // Validate database exists
        // Set currentDatabase
        return "[OK] Using database";
    }

    private String handleCreateCommand(String command) {
        // Implementation for CREATE DATABASE/TABLE command
        if (command.toUpperCase().contains("DATABASE")) {
            // Handle CREATE DATABASE
            return "[OK] Database created";
        } else if (command.toUpperCase().contains("TABLE")) {
            // Handle CREATE TABLE
            return "[OK] Table created";
        }
        return "[ERROR] Invalid CREATE command";
    }

    private String handleInsertCommand(String command) {
        // Implementation for INSERT INTO command
        return "[OK] Record inserted";
    }

    private String handleSelectCommand(String command) {
        // Implementation for SELECT command
        return "[OK] Query results";
    }

    private String handleUpdateCommand(String command) {
        // Implementation for UPDATE command
        return "[OK] Records updated";
    }

    private String handleDeleteCommand(String command) {
        // Implementation for DELETE command
        return "[OK] Records deleted";
    }

    private String handleDropCommand(String command) {
        // Implementation for DROP command
        return "[OK] Item dropped";
    }

    private String handleAlterCommand(String command) {
        // Implementation for ALTER command
        return "[OK] Table altered";
    }

    private String handleJoinCommand(String command) {
        // Implementation for JOIN command
        return "[OK] Tables joined";
    }

    // Helper methods for file operations
    private void saveTable(String database, String tableName, String tableContent) {
        // Save table to file system
        try {
            String dbPath = storageFolderPath + File.separator + database.toLowerCase();
            Files.createDirectories(Paths.get(dbPath));

            String tablePath = dbPath + File.separator + tableName.toLowerCase() + ".tab";
            Files.writeString(Paths.get(tablePath), tableContent);
        } catch (IOException e) {
            System.err.println("Error saving table: " + e.getMessage());
        }
    }

    private String loadTable(String database, String tableName) {
        // Load table from file system
        try {
            String tablePath = storageFolderPath + File.separator +
                    database.toLowerCase() + File.separator +
                    tableName.toLowerCase() + ".tab";
            if (Files.exists(Paths.get(tablePath))) {
                return Files.readString(Paths.get(tablePath));
            }
        } catch (IOException e) {
            System.err.println("Error loading table: " + e.getMessage());
        }
        return null;
    }

    //  === Methods below handle networking aspects of the project - you will not need to change these ! ===

    public void blockingListenOn(int portNumber) throws IOException {
        try (ServerSocket s = new ServerSocket(portNumber)) {
            System.out.println("Server listening on port " + portNumber);
            while (!Thread.interrupted()) {
                try {
                    blockingHandleConnection(s);
                } catch (IOException e) {
                    System.err.println("Server encountered a non-fatal IO error:");
                    e.printStackTrace();
                    System.err.println("Continuing...");
                }
            }
        }
    }

    private void blockingHandleConnection(ServerSocket serverSocket) throws IOException {
        try (Socket s = serverSocket.accept();
             BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()))) {

            System.out.println("Connection established: " + serverSocket.getInetAddress());
            while (!Thread.interrupted()) {
                String incomingCommand = reader.readLine();
                System.out.println("Received message: " + incomingCommand);
                String result = handleCommand(incomingCommand);
                writer.write(result);
                writer.write("\n" + END_OF_TRANSMISSION + "\n");
                writer.flush();
            }
        }
    }
}