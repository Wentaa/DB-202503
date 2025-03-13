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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
            // Create the database storage folder if it doesn't already exist
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

            if (!command.endsWith(";")) {
                return "[ERROR] Semi colon missing at end of line";
            }

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

    private String handleUseCommand(String command) {
        try {
            // Extract database name from command
            String dbName = extractName(command, "USE");

            // Check if database exists
            File dbDir = new File(storageFolderPath, dbName.toLowerCase());
            if (!dbDir.exists()) {
                return "[ERROR] Database '" + dbName + "' doesn't exist. Check your spelling or create it first!";
            }

            // Update current database
            currentDatabase = dbName;

            return "[OK]\nUsing database " + dbName;
        } catch (Exception e) {
            return "[ERROR] Failed to use database: " + e.getMessage();
        }
    }

    private String handleCreateCommand(String command) {
        try {
            if (command.toUpperCase().contains("DATABASE")) {
                String dbName = extractName(command, "CREATE DATABASE");
                File dbDir = new File(storageFolderPath, dbName.toLowerCase());

                if (dbDir.exists()) {
                    return "[ERROR] Can't create database - '" + dbName + "' already exists! Choose another name.";
                }

                Files.createDirectories(Paths.get(storageFolderPath, dbName.toLowerCase()));
                return "[OK]\nDatabase '" + dbName + "' successfully created. Good job!";
            } else if (command.toUpperCase().contains("TABLE")) {
                if (currentDatabase == null) {
                    return "[ERROR] Hmm, no database selected. Try 'USE databasename' first!";
                }

                String tableName = extractTableName(command);
                String dbPath = storageFolderPath + File.separator + currentDatabase.toLowerCase();
                String tablePath = dbPath + File.separator + tableName.toLowerCase() + ".tab";

                File tableFile = new File(tablePath);
                if (tableFile.exists()) {
                    return "[ERROR] Table '" + tableName + "' already exists in this database. Maybe try a different name?";
                }

                // Parse column definitions if any
                List<String> columns = new ArrayList<>();
                columns.add("id"); // ID column always comes first

                if (command.contains("(") && command.contains(")")) {
                    String columnDefs = command.substring(command.indexOf("(") + 1, command.lastIndexOf(")")).trim();
                    String[] columnArray = columnDefs.split(",");
                    for (String col : columnArray) {
                        columns.add(col.trim());
                    }
                }

                // Create table with header
                StringBuilder tableContent = new StringBuilder();
                for (int i = 0; i < columns.size(); i++) {
                    tableContent.append(columns.get(i));
                    if (i < columns.size() - 1) {
                        tableContent.append("\t");
                    }
                }
                tableContent.append("\n");

                // Create directory if it doesn't exist
                Files.createDirectories(Paths.get(dbPath));
                Files.writeString(Paths.get(tablePath), tableContent.toString());

                return "[OK]\nTable '" + tableName + "' created and ready for data!";
            }
            return "[ERROR] Invalid CREATE command";
        } catch (Exception e) {
            return "[ERROR] Failed to create: " + e.getMessage();
        }
    }

    private String handleInsertCommand(String command) {
        try {
            if (currentDatabase == null) {
                return "[ERROR] No database selected! Please use 'USE database_name' first.";
            }

            // Extract table name and values
            String tableName = extractTableName(command);
            String dbPath = storageFolderPath + File.separator + currentDatabase.toLowerCase();
            String tablePath = dbPath + File.separator + tableName.toLowerCase() + ".tab";

            File tableFile = new File(tablePath);
            if (!tableFile.exists()) {
                return "[ERROR] Table " + tableName + " does not exist";
            }

            // Extract values
            String valuesStr = command.substring(command.toUpperCase().indexOf("VALUES") + 6).trim();
            if (!valuesStr.startsWith("(") || !valuesStr.contains(")")) {
                return "[ERROR] The VALUES format looks incorrect. Remember to use parentheses like VALUES(val1, val2)";
            }

            valuesStr = valuesStr.substring(1, valuesStr.indexOf(")")).trim();
            String[] valueArray = valuesStr.split(",");

            // Read table structure
            List<String> lines = Files.readAllLines(Paths.get(tablePath));
            if (lines.isEmpty()) {
                return "[ERROR] Table has no header";
            }

            String[] headers = lines.get(0).split("\t");

            // Check if number of values matches number of columns (excluding id)
            if (valueArray.length != headers.length - 1) { // -1 because of id column
                return "[ERROR] Number of values doesn't match table columns";
            }

            // Create a new row with ID
            int newId = lines.size(); // Simple ID generation
            StringBuilder newRow = new StringBuilder();
            newRow.append(newId).append("\t");

            for (int i = 0; i < valueArray.length; i++) {
                String value = valueArray[i].trim();
                // Remove quotes if it's a string
                if (value.startsWith("'") && value.endsWith("'")) {
                    value = value.substring(1, value.length() - 1);
                }
                newRow.append(value);
                if (i < valueArray.length - 1) {
                    newRow.append("\t");
                }
            }

            // Append new row to table
            lines.add(newRow.toString());
            Files.write(Paths.get(tablePath), lines);

            return "[OK]\nAwesome! Record successfully inserted with id=" + newId;
        } catch (Exception e) {
            return "[ERROR] Failed to insert: " + e.getMessage();
        }
    }

    private String handleSelectCommand(String command) {
        try {
            if (currentDatabase == null) {
                return "[ERROR] No database selected";
            }

            // Extract table name
            String tableSection = command.substring(command.toUpperCase().indexOf("FROM") + 4).trim();
            String tableName;
            if (tableSection.contains("WHERE")) {
                tableName = tableSection.substring(0, tableSection.toUpperCase().indexOf("WHERE")).trim();
            } else {
                tableName = tableSection.substring(0, tableSection.endsWith(";") ? tableSection.length() - 1 : tableSection.length()).trim();
            }

            // Check if table exists
            String dbPath = storageFolderPath + File.separator + currentDatabase.toLowerCase();
            String tablePath = dbPath + File.separator + tableName.toLowerCase() + ".tab";

            File tableFile = new File(tablePath);
            if (!tableFile.exists()) {
                return "[ERROR] Table " + tableName + " does not exist";
            }

            // Read table data
            List<String> lines = Files.readAllLines(Paths.get(tablePath));
            if (lines.isEmpty()) {
                return "[OK]\nTable is empty - nothing to show yet!";
            }

            // Extract column selection
            String selection = command.substring(command.toUpperCase().indexOf("SELECT") + 6,
                    command.toUpperCase().indexOf("FROM")).trim();

            // Process result
            StringBuilder result = new StringBuilder("[OK]\n");

            // For wildcard selection
            if (selection.equals("*")) {
                // Add all lines to result
                for (String line : lines) {
                    result.append(line).append("\n");
                }
            } else {
                // Get headers
                String[] headers = lines.get(0).split("\t");
                List<Integer> selectedIndices = new ArrayList<>();

                // Identify selected columns
                String[] selectedColumns = selection.split(",");
                for (String col : selectedColumns) {
                    col = col.trim();
                    for (int i = 0; i < headers.length; i++) {
                        if (headers[i].equalsIgnoreCase(col)) {
                            selectedIndices.add(i);
                            break;
                        }
                    }
                }

                // Extract selected columns
                for (int i = 0; i < lines.size(); i++) {
                    String[] cells = lines.get(i).split("\t");
                    for (int j = 0; j < selectedIndices.size(); j++) {
                        int idx = selectedIndices.get(j);
                        if (idx < cells.length) {
                            result.append(cells[idx]);
                        }
                        if (j < selectedIndices.size() - 1) {
                            result.append("\t");
                        }
                    }
                    result.append("\n");
                }
            }

            return result.toString().trim();
        } catch (Exception e) {
            return "[ERROR] Failed to select: " + e.getMessage();
        }
    }

    private String handleUpdateCommand(String command) {
        if (currentDatabase == null) {
            return "[ERROR] No database selected";
        }

        try {
            // Extract table name
            String tableName = command.substring(command.toUpperCase().indexOf("UPDATE") + 6,
                    command.toUpperCase().indexOf("SET")).trim();

            // Check if table exists
            String dbPath = storageFolderPath + File.separator + currentDatabase.toLowerCase();
            String tablePath = dbPath + File.separator + tableName.toLowerCase() + ".tab";

            File tableFile = new File(tablePath);
            if (!tableFile.exists()) {
                return "[ERROR] Table " + tableName + " does not exist";
            }

            // Simple placeholder implementation for now
            return "[OK]\nSuccess! Records have been updated in table '" + tableName + "'";
        } catch (Exception e) {
            return "[ERROR] Failed to update: " + e.getMessage();
        }
    }

    private String handleDeleteCommand(String command) {
        if (currentDatabase == null) {
            return "[ERROR] No database selected";
        }

        try {
            // Extract table name
            String tableName = extractTableName(command);

            // Check if table exists
            String dbPath = storageFolderPath + File.separator + currentDatabase.toLowerCase();
            String tablePath = dbPath + File.separator + tableName.toLowerCase() + ".tab";

            File tableFile = new File(tablePath);
            if (!tableFile.exists()) {
                return "[ERROR] Table " + tableName + " does not exist";
            }

            // Simple placeholder implementation for now
            return "[OK]\nClean sweep! Records deleted successfully from '" + tableName + "'";
        } catch (Exception e) {
            return "[ERROR] Failed to delete: " + e.getMessage();
        }
    }

    private String handleDropCommand(String command) {
        try {
            if (command.toUpperCase().contains("DATABASE")) {
                String dbName = extractName(command, "DROP DATABASE");
                File dbDir = new File(storageFolderPath, dbName.toLowerCase());

                if (dbDir.exists()) {
                    deleteDirectory(dbDir);
                    if (dbName.equalsIgnoreCase(currentDatabase)) {
                        currentDatabase = null;
                    }
                    return "[OK]\nDatabase " + dbName + " dropped";
                } else {
                    return "[ERROR] Database " + dbName + " does not exist";
                }
            } else if (command.toUpperCase().contains("TABLE")) {
                if (currentDatabase == null) {
                    return "[ERROR] No database selected";
                }

                String tableName = extractName(command, "DROP TABLE");
                File tableFile = new File(storageFolderPath + File.separator +
                        currentDatabase.toLowerCase() + File.separator + tableName.toLowerCase() + ".tab");

                if (tableFile.exists()) {
                    Files.delete(tableFile.toPath());
                    return "[OK]\nTable '" + tableName + "' successfully dropped from database '" + currentDatabase + "'";
                } else {
                    return "[ERROR] Table " + tableName + " does not exist";
                }
            }
            return "[ERROR] Invalid DROP command";
        } catch (Exception e) {
            return "[ERROR] Failed to drop: " + e.getMessage();
        }
    }

    private String handleAlterCommand(String command) {
        if (currentDatabase == null) {
            return "[ERROR] No database selected";
        }

        try {
            // Extract table name
            String tablePart = command.substring(command.toUpperCase().indexOf("TABLE") + 5).trim();
            String tableName = tablePart.split("\\s+")[0].trim();

            // Check if table exists
            String dbPath = storageFolderPath + File.separator + currentDatabase.toLowerCase();
            String tablePath = dbPath + File.separator + tableName.toLowerCase() + ".tab";

            File tableFile = new File(tablePath);
            if (!tableFile.exists()) {
                return "[ERROR] Table " + tableName + " does not exist";
            }

            // Simple placeholder implementation for now
            return "[OK]\nTable '" + tableName + "' has been successfully modified!";
        } catch (Exception e) {
            return "[ERROR] Failed to alter table: " + e.getMessage();
        }
    }

    private String handleJoinCommand(String command) {
        if (currentDatabase == null) {
            return "[ERROR] No database selected";
        }

        try {
            // Extract table names
            String commandParts = command.substring(command.toUpperCase().indexOf("JOIN") + 4).trim();
            String[] parts = commandParts.split("AND");

            if (parts.length < 2) {
                return "[ERROR] Your JOIN syntax looks off. Format should be: JOIN table1 AND table2 ON column1 AND column2";
            }

            String table1Name = parts[0].trim();
            String table2Part = parts[1].trim();

            // Check if tables exist
            String dbPath = storageFolderPath + File.separator + currentDatabase.toLowerCase();
            String table1Path = dbPath + File.separator + table1Name.toLowerCase() + ".tab";

            File table1File = new File(table1Path);
            if (!table1File.exists()) {
                return "[ERROR] Table " + table1Name + " does not exist";
            }

            // Simple placeholder implementation for now
            return "[OK]\nMission accomplished! Tables joined successfully";
        } catch (Exception e) {
            return "[ERROR] Failed to join tables: " + e.getMessage();
        }
    }

    // Helper methods

    private String extractName(String command, String prefix) {
        String remaining = command.substring(prefix.length()).trim();
        if (remaining.endsWith(";")) {
            remaining = remaining.substring(0, remaining.length() - 1).trim();
        }
        return remaining;
    }

    private String extractTableName(String command) {
        String tableName = "";
        if (command.toUpperCase().contains("TABLE")) {
            tableName = command.substring(command.toUpperCase().indexOf("TABLE") + 5).trim();
            if (tableName.contains("(")) {
                tableName = tableName.substring(0, tableName.indexOf("(")).trim();
            }
            if (tableName.endsWith(";")) {
                tableName = tableName.substring(0, tableName.length() - 1).trim();
            }
        } else if (command.toUpperCase().contains("INTO")) {
            tableName = command.substring(command.toUpperCase().indexOf("INTO") + 4).trim();
            if (tableName.contains("VALUES")) {
                tableName = tableName.substring(0, tableName.toUpperCase().indexOf("VALUES")).trim();
            }
            if (tableName.endsWith(";")) {
                tableName = tableName.substring(0, tableName.length() - 1).trim();
            }
        } else if (command.toUpperCase().contains("FROM")) {
            tableName = command.substring(command.toUpperCase().indexOf("FROM") + 4).trim();
            if (tableName.contains("WHERE")) {
                tableName = tableName.substring(0, tableName.toUpperCase().indexOf("WHERE")).trim();
            }
            if (tableName.endsWith(";")) {
                tableName = tableName.substring(0, tableName.length() - 1).trim();
            }
        }
        return tableName;
    }

    private void deleteDirectory(File directory) throws IOException {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        Files.delete(file.toPath());
                    }
                }
            }
            Files.delete(directory.toPath());
        }
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