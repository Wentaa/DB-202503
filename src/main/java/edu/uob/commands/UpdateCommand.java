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

/**
 * Handles the SQL `UPDATE` command for modifying existing rows in a table.
 * Supports updating multiple columns in rows that match a given condition.
 */
public class UpdateCommand extends Command {
    private String tableName;            // Name of the table to update
    private Map<String, String> assignments; // Key-value pairs of columns and new values
    private Condition condition;         // Optional condition to filter which rows should be updated

    /**
     * Constructs an `UPDATE` command.
     *
     * @param tableName   The name of the table where updates will be applied.
     * @param assignments A map of column names and their new values.
     * @param condition   An optional condition to specify which rows should be updated.
     */
    public UpdateCommand(String tableName, Map<String, String> assignments, Condition condition) {
        this.tableName = tableName;
        this.assignments = assignments;
        this.condition = condition;
    }

    /**
     * Executes the `UPDATE` command.
     * It identifies the rows that match the condition, then updates the specified columns.
     *
     * @param dbManager The database manager handling the update operation.
     * @return A `QueryResult` indicating the success of the operation.
     * @throws RuntimeException if no database is selected, the table does not exist,
     *                          a specified column is not found, or an attempt is made to update the ID column.
     */
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

            // Prevent updates to the ID column
            if (assignments.containsKey("id")) {
                throw new RuntimeException("Cannot update ID column");
            }

            // Validate that all specified columns exist
            for (String colName : assignments.keySet()) {
                if (table.getColumnIndex(colName) == -1) {
                    throw new RuntimeException("Column not found: " + colName);
                }
            }

            // Identify rows that match the condition
            List<Integer> updatedRowIds = new ArrayList<>();
            for (Row row : table.getRows()) {
                if (condition == null || condition.evaluate(table, row)) {
                    updatedRowIds.add(row.getId());
                }
            }

            // Apply updates to the selected rows
            for (int rowId : updatedRowIds) {
                dbManager.updateRow(table, rowId, assignments);
            }

            return new QueryResult(); // Return an empty QueryResult to indicate success
        } catch (Exception e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }
}
