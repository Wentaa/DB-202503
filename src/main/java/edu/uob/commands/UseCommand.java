package edu.uob.commands;

import edu.uob.models.QueryResult;
import edu.uob.storage.DBManager;

/**
 * Handles the SQL `USE` command for switching to a different database.
 */
public class UseCommand extends Command {
    private String databaseName; // Name of the database to switch to

    /**
     * Constructs a `USE` command.
     *
     * @param databaseName The name of the database to be used.
     */
    public UseCommand(String databaseName) {
        this.databaseName = databaseName;
    }

    /**
     * Executes the `USE` command.
     * Attempts to switch the active database to the specified one.
     *
     * @param dbManager The database manager handling the operation.
     * @return A `QueryResult` indicating the success of the operation.
     * @throws RuntimeException if the specified database does not exist.
     */
    @Override
    public QueryResult execute(DBManager dbManager) {
        try {
            // Attempt to switch to the specified database
            dbManager.useDatabase(databaseName);
            return new QueryResult(); // Return an empty QueryResult to indicate success
        } catch (IllegalArgumentException e) {
            // Handle case where the database does not exist
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }
}
