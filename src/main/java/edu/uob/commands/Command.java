// Command.java
package edu.uob.commands;

import edu.uob.models.QueryResult;
import edu.uob.storage.DBManager;

public abstract class Command {
    /**
     * Execute the command with the given database manager
     * @param dbManager The database manager
     * @return A QueryResult object containing the result of the command
     */
    public abstract QueryResult execute(DBManager dbManager);
}