package edu.uob.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a database that contains multiple tables.
 * Provides methods for managing tables, including creation, retrieval, and deletion.
 */
public class Database {
    private String name; // The name of the database (stored in lowercase for case insensitivity)
    private Map<String, Table> tables; // A mapping of table names to their corresponding Table objects

    /**
     * Constructs a new `Database` with a given name.
     * The name is stored in lowercase to ensure case-insensitive table management.
     *
     * @param name The name of the database.
     */
    public Database(String name) {
        this.name = name.toLowerCase();
        this.tables = new HashMap<>();
    }

    /**
     * Gets the name of the database.
     *
     * @return The database name.
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves all tables in the database.
     *
     * @return A map of table names to their corresponding `Table` objects.
     */
    public Map<String, Table> getTables() {
        return tables;
    }

    /**
     * Adds a new table to the database.
     *
     * @param table The table to be added.
     */
    public void addTable(Table table) {
        tables.put(table.getName().toLowerCase(), table);
    }

    /**
     * Retrieves a table by name.
     *
     * @param tableName The name of the table to retrieve.
     * @return The `Table` object if found, otherwise `null`.
     */
    public Table getTable(String tableName) {
        return tables.get(tableName.toLowerCase());
    }

    /**
     * Removes a table from the database.
     *
     * @param tableName The name of the table to drop.
     */
    public void dropTable(String tableName) {
        tables.remove(tableName.toLowerCase());
    }

    /**
     * Checks if a table exists in the database.
     *
     * @param tableName The name of the table to check.
     * @return `true` if the table exists, otherwise `false`.
     */
    public boolean hasTable(String tableName) {
        return tables.containsKey(tableName.toLowerCase());
    }

    /**
     * Creates a new table with the specified columns and adds it to the database.
     *
     * @param tableName   The name of the new table.
     * @param columnNames A list of column names to be included in the table.
     */
    public void createTable(String tableName, List<String> columnNames) {
        Table table = new Table(tableName);
        for (String columnName : columnNames) {
            table.addColumn(columnName);
        }
        tables.put(tableName.toLowerCase(), table);
    }
}
