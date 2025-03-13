// AlterCommand.java
package edu.uob.commands;

import edu.uob.models.Database;
import edu.uob.models.QueryResult;
import edu.uob.models.Table;
import edu.uob.storage.DBManager;

public class AlterCommand extends Command {
    private String tableName;
    private String columnName;
    private boolean isAdd;

    public AlterCommand(String tableName, String columnName, boolean isAdd) {
        this.tableName = tableName;
        this.columnName = columnName;
        this.isAdd = isAdd;
    }

    @Override
    public QueryResult execute(DBManager dbManager) {
        try {
            Database currentDb = dbManager.getCurrentDatabase();
            if (currentDb == null) {
                throw new RuntimeException("No database selected");
            }

            Table table = currentDb.getTable(tableName);
            if (table == null) {
                throw new RuntimeException("Table does not exist: " + tableName);
            }

            if (isAdd) {
                // Check if column already exists
                if (table.hasColumn(columnName)) {
                    throw new RuntimeException("Column already exists: " + columnName);
                }

                table.addColumn(columnName);
            } else {
                // Check if trying to drop ID column
                if (columnName.equalsIgnoreCase("id")) {
                    throw new RuntimeException("Cannot drop ID column");
                }

                // Check if column exists
                if (!table.hasColumn(columnName)) {
                    throw new RuntimeException("Column does not exist: " + columnName);
                }

                table.dropColumn(columnName);
            }

            // Save the updated table
            String dbPath = dbManager.getDatabasePath(currentDb.getName());
            dbManager.saveTable(table, dbPath);

            QueryResult result = new QueryResult();
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }
}