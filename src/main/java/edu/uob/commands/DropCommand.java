// DropCommand.java - completed
package edu.uob.commands;

import edu.uob.models.Database;
import edu.uob.models.QueryResult;
import edu.uob.models.Table;
import edu.uob.storage.DBManager;

public class DropCommand extends Command {
    private String name;
    private boolean isDatabase;

    public DropCommand(String name, boolean isDatabase) {
        this.name = name;
        this.isDatabase = isDatabase;
    }

    @Override
    public QueryResult execute(DBManager dbManager) {
        try {
            if (isDatabase) {
                dbManager.dropDatabase(name);
            } else {
                Database currentDb = dbManager.getCurrentDatabase();
                if (currentDb == null) {
                    throw new RuntimeException("No database selected");
                }

                if (!currentDb.hasTable(name)) {
                    throw new RuntimeException("Table does not exist: " + name);
                }

                String dbPath = dbManager.getDatabasePath(currentDb.getName());
                Table table = currentDb.getTable(name);
                dbManager.dropTable(table, dbPath);
            }

            QueryResult result = new QueryResult();
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }
}