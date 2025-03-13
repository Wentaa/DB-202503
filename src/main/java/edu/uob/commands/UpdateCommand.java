// UpdateCommand.java
package edu.uob.commands;

import edu.uob.conditions.Condition;
import edu.uob.models.Database;
import edu.uob.models.QueryResult;
import edu.uob.models.Row;
import edu.uob.models.Table;
import edu.uob.storage.DBManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UpdateCommand extends Command {
    private String tableName;
    private Map<String, String> assignments;
    private Condition condition;

    public UpdateCommand(String tableName, Map<String, String> assignments, Condition condition) {
        this.tableName = tableName;
        this.assignments = assignments;
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

            // Check if trying to update ID
            if (assignments.containsKey("id")) {
                throw new RuntimeException("Cannot update ID column");
            }

            // Check if columns exist
            for (String colName : assignments.keySet()) {
                if (table.getColumnIndex(colName) == -1) {
                    throw new RuntimeException("Column not found: " + colName);
                }
            }

            // Update matching rows
            List<Integer> updatedRowIds = new ArrayList<>();
            for (Row row : table.getRows()) {
                if (condition == null || condition.evaluate(table, row)) {
                    updatedRowIds.add(row.getId());
                }
            }

            for (int rowId : updatedRowIds) {
                dbManager.updateRow(table, rowId, assignments);
            }

            QueryResult result = new QueryResult();
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }
}