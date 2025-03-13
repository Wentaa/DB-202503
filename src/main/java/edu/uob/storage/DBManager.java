package edu.uob.storage;

import edu.uob.models.Database;
import edu.uob.models.Table;
import edu.uob.parser.SQLKeywords;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBManager {
    private Map<String, Database> databases;
    private String currentDatabase;
    private String storageFolderPath;

    public DBManager(String storageFolderPath) {
        this.databases = new HashMap<>();
        this.storageFolderPath = storageFolderPath;

        // Create storage folder
        File storageFolder = new File(storageFolderPath);
        if (!storageFolder.exists()) {
            storageFolder.mkdirs();
        }

        // Load existing databases
        loadDatabases();
    }

    private void loadDatabases() {
        File storageFolder = new File(storageFolderPath);
        File[] dbFolders = storageFolder.listFiles(File::isDirectory);

        if (dbFolders != null) {
            for (File dbFolder : dbFolders) {
                String dbName = dbFolder.getName().toLowerCase();
                Database db = new Database(dbName);

                // Load tables
                File[] tableFiles = dbFolder.listFiles((dir, name) -> name.endsWith(".tab"));

                if (tableFiles != null) {
                    for (File tableFile : tableFiles) {
                        String tableName = tableFile.getName();
                        // Remove .tab extension
                        tableName = tableName.substring(0, tableName.length() - 4);

                        try {
                            Table table = TableFileIO.loadTable(tableName, dbFolder.getPath());
                            db.addTable(table);
                        } catch (IOException e) {
                            // Just print error and continue loading other tables
                            System.err.println("Error loading table " + tableName + ": " + e.getMessage());
                        }
                    }
                }

                databases.put(dbName, db);
            }
        }
    }

    public void createDatabase(String dbName) {
        dbName = dbName.toLowerCase();
        if (databases.containsKey(dbName)) {
            throw new IllegalArgumentException("Database already exists: " + dbName);
        }

        // Create database folder
        File dbFolder = new File(storageFolderPath + File.separator + dbName);
        if (!dbFolder.exists()) {
            dbFolder.mkdirs();
        }

        // Add to memory
        databases.put(dbName, new Database(dbName));
    }

    public void dropDatabase(String dbName) {
        dbName = dbName.toLowerCase();
        if (!databases.containsKey(dbName)) {
            throw new IllegalArgumentException("Database does not exist: " + dbName);
        }

        // Delete database folder
        File dbFolder = new File(storageFolderPath + File.separator + dbName);
        deleteFolder(dbFolder);

        // Remove from memory
        databases.remove(dbName);

        // Reset current database if it was deleted
        if (dbName.equals(currentDatabase)) {
            currentDatabase = null;
        }
    }

    private void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteFolder(file);
                } else {
                    file.delete();
                }
            }
        }
        folder.delete();
    }

    public void useDatabase(String dbName) {
        dbName = dbName.toLowerCase();
        if (!databases.containsKey(dbName)) {
            throw new IllegalArgumentException("Database does not exist: " + dbName);
        }

        currentDatabase = dbName;
    }

    public void createTable(String tableName, List<String> columnNames) {
        if (currentDatabase == null) {
            throw new IllegalStateException("No database selected");
        }

        tableName = tableName.toLowerCase();
        Database db = databases.get(currentDatabase);

        if (db.hasTable(tableName)) {
            throw new IllegalArgumentException("Table already exists: " + tableName);
        }

        // Validate column names for SQL keywords
        for (String colName : columnNames) {
            if (SQLKeywords.isKeyword(colName)) {
                throw new IllegalArgumentException("Cannot use SQL keyword as column name: " + colName);
            }
        }

        // Create table
        Table table = new Table(tableName);
        for (String colName : columnNames) {
            table.addColumn(colName);
        }

        // Save table
        String dbPath = storageFolderPath + File.separator + currentDatabase;
        try {
            TableFileIO.saveTable(table, dbPath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save table: " + e.getMessage());
        }

        // Add to memory
        db.addTable(table);
    }

    public void dropTable(Table table, String dbPath) {
        // Delete table file
        File tableFile = new File(dbPath + File.separator + table.getName() + ".tab");
        if (tableFile.exists()) {
            tableFile.delete();
        }

        // Remove from memory
        Database currentDb = databases.get(currentDatabase);
        currentDb.dropTable(table.getName());
    }

    public void saveTable(Table table, String dbPath) {
        try {
            TableFileIO.saveTable(table, dbPath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save table: " + e.getMessage());
        }
    }

    public Database getCurrentDatabase() {
        if (currentDatabase == null) {
            return null;
        }
        return databases.get(currentDatabase);
    }

    public String getCurrentDatabaseName() {
        return currentDatabase;
    }

    public Table getTable(String tableName) {
        if (currentDatabase == null) {
            throw new IllegalStateException("No database selected");
        }

        Database db = databases.get(currentDatabase);
        return db.getTable(tableName.toLowerCase());
    }

    public boolean hasDatabase(String dbName) {
        return databases.containsKey(dbName.toLowerCase());
    }

    public String getDatabasePath(String dbName) {
        return storageFolderPath + File.separator + dbName.toLowerCase();
    }

    public void insertRow(String tableName, List<String> values) {
        if (currentDatabase == null) {
            throw new IllegalStateException("No database selected");
        }

        Database db = databases.get(currentDatabase);
        Table table = db.getTable(tableName.toLowerCase());

        if (table == null) {
            throw new IllegalArgumentException("Table does not exist: " + tableName);
        }

        // Check if the number of values matches columns
        if (values.size() != table.getColumns().size() - 1) { // -1 for ID
            throw new IllegalArgumentException("Value count doesn't match column count");
        }

        // Add row to table
        table.addRow(values);

        // Save table
        String dbPath = getDatabasePath(currentDatabase);
        saveTable(table, dbPath);
    }

    public void updateRow(Table table, int rowId, Map<String, String> assignments) {
        table.updateRow(rowId, assignments);

        // Save table
        String dbPath = getDatabasePath(currentDatabase);
        saveTable(table, dbPath);
    }

    public void deleteRows(Table table, List<Integer> rowIds) {
        for (int id : rowIds) {
            table.deleteRow(id);
        }

        // Save table
        String dbPath = getDatabasePath(currentDatabase);
        saveTable(table, dbPath);
    }
}