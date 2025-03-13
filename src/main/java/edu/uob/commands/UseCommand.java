// UseCommand.java
package edu.uob.commands;

import edu.uob.models.QueryResult;
import edu.uob.storage.DBManager;

public class UseCommand extends Command {
    private String databaseName;

    public UseCommand(String databaseName) {
        this.databaseName = databaseName;
    }

    @Override
    public QueryResult execute(DBManager dbManager) {
        try {
            dbManager.useDatabase(databaseName);
            QueryResult result = new QueryResult();
            return result;
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }
}