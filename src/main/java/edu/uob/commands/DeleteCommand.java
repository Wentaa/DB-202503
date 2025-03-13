// DeleteCommand.java
package edu.uob.commands;

import edu.uob.conditions.Condition;
import edu.uob.models.Database;
import edu.uob.models.QueryResult;
import edu.uob.models.Row;
import edu.uob.models.Table;
import edu.uob.storage.DBManager;

import java.util.ArrayList;
import java.util.List;

public class DeleteCommand extends Command {
    private String tableName;
    private Condition condition;

    public DeleteCommand(String tableName, Condition condition) {
        this.tableName = tableName;
        this.condition = condition;
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

            // Find rows to delete
            List<Integer> rowIdsToDelete = new ArrayList<>();
            for (Row row : table.getRows()) {
                if (condition.evaluate(table, row)) {
                    rowIdsToDelete.add(row.getId());
                }
            }

            // Delete rows
            dbManager.deleteRows(table, rowIdsToDelete);

            QueryResult result = new QueryResult();
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }
}