// CreateCommand.java
package edu.uob.commands;

import edu.uob.models.QueryResult;
import edu.uob.storage.DBManager;

import java.util.List;

public class CreateCommand extends Command {
    private String name;
    private List<String> columnNames;
    private boolean isDatabase;

    public CreateCommand(String name, List<String> columnNames) {
        this.name = name;
        this.columnNames = columnNames;
        this.isDatabase = (columnNames == null);
    }

    @Override
    public QueryResult execute(DBManager dbManager) {
        try {
            if (isDatabase) {
                dbManager.createDatabase(name);
            } else {
                dbManager.createTable(name, columnNames);
            }
            QueryResult result = new QueryResult();
            return result;
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }
}