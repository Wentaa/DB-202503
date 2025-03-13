package edu.uob.commands;

import edu.uob.models.Database;
import edu.uob.models.QueryResult;
import edu.uob.models.Table;
import edu.uob.storage.DBManager;

/**
 * Handles the SQL `DROP` command for removing databases or tables.
 */
public class DropCommand extends Command {
    private String name;        // Name of the database or table to be dropped
    private boolean isDatabase; // True if dropping a database, false if dropping a table

    /**
     * Constructs a `DROP` command.
     *
     * @param name       The name of the database or table to drop.
     * @param isDatabase True if the command is for dropping a database, false for a table.
     */
    public DropCommand(String name, boolean isDatabase) {
        this.name = name;
        this.isDatabase = isDatabase;
    }

    /**
     * Executes the `DROP` command.
     * It either removes an entire database or a specific table.
     *
     * @param dbManager The database manager handling the operation.
     * @return A `QueryResult` indicating the success of the operation.
     * @throws RuntimeException if no database is selected (when dropping a table) or if the table does not exist.
     */
    @Override
    public QueryResult execute(DBManager dbManager) {
        try {
            if (isDatabase) {
                // Drop the entire database
                dbManager.dropDatabase(name);
            } else {
                // Drop a specific table
                Database currentDb = dbManager.getCurrentDatabase();
                if (currentDb == null) {
                    throw new RuntimeException("No database selected");
                }

                if (!currentDb.hasTable(name)) {
                    throw new RuntimeException("Table does not exist: " + name);
                }

                // Get the database path and drop the table
                String dbPath = dbManager.getDatabasePath(currentDb.getName());
                Table table = currentDb.getTable(name);
                dbManager.dropTable(table, dbPath);
            }

            return new QueryResult(); // Return an empty QueryResult to indicate success
        } catch (Exception e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }
}
