package edu.uob.commands;

import edu.uob.models.QueryResult;
import edu.uob.storage.DBManager;

/**
 * Abstract base class for all database commands.
 * Each command must implement the `execute` method to perform the required operation.
 */
public abstract class Command {

    /**
     * Executes the command using the provided database manager.
     * Each subclass defines its own behavior for execution.
     *
     * @param dbManager The database manager that provides access to the database.
     * @return A {@code QueryResult} object containing the outcome of the command execution.
     */
    public abstract QueryResult execute(DBManager dbManager);
}
