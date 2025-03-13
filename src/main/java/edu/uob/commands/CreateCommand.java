package edu.uob.commands;

import edu.uob.models.QueryResult;
import edu.uob.storage.DBManager;

import java.util.List;

/**
 * Handles the SQL `CREATE` command for creating databases and tables.
 * If `columnNames` is null, it creates a database; otherwise, it creates a table.
 */
public class CreateCommand extends Command {
    private String name;            // Name of the database or table to be created
    private List<String> columnNames; // List of column names (if creating a table)
    private boolean isDatabase;      // True if creating a database, false if creating a table

    /**
     * Constructs a `CREATE` command.
     *
     * @param name        The name of the database or table to create.
     * @param columnNames The list of column names if creating a table, or null if creating a database.
     */
    public CreateCommand(String name, List<String> columnNames) {
        this.name = name;
        this.columnNames = columnNames;
        this.isDatabase = (columnNames == null); // Determines whether it's a database or table creation
    }

    /**
     * Executes the `CREATE` command.
     * It either creates a new database or a new table with the specified columns.
     *
     * @param dbManager The database manager that handles the operation.
     * @return A `QueryResult` indicating the success of the operation.
     * @throws RuntimeException if there is an error (e.g., name conflicts, invalid column names).
     */
    @Override
    public QueryResult execute(DBManager dbManager) {
        try {
            if (isDatabase) {
                // Create a new database with the specified name
                dbManager.createDatabase(name);
            } else {
                // Create a new table with the specified columns
                dbManager.createTable(name, columnNames);
            }
            return new QueryResult(); // Return an empty result indicating success
        } catch (IllegalArgumentException e) {
            // Handle potential issues like name conflicts or invalid column names
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }
}
