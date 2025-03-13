package edu.uob.commands;

import edu.uob.conditions.Condition;
import edu.uob.models.Database;
import edu.uob.models.QueryResult;
import edu.uob.models.Row;
import edu.uob.models.Table;
import edu.uob.storage.DBManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles the SQL `DELETE` command for removing rows from a table
 * based on a specified condition.
 */
public class DeleteCommand extends Command {
    private String tableName;  // Name of the table from which rows will be deleted
    private Condition condition;  // Condition to determine which rows should be deleted

    /**
     * Constructs a `DELETE` command.
     *
     * @param tableName The name of the table from which rows should be deleted.
     * @param condition The condition that determines which rows to delete.
     */
    public DeleteCommand(String tableName, Condition condition) {
        this.tableName = tableName;
        this.condition = condition;
    }

    /**
     * Executes the `DELETE` command.
     * It evaluates each row against the condition and removes matching rows.
     *
     * @param dbManager The database manager that provides access to the current database.
     * @return A `QueryResult` indicating the success of the operation.
     * @throws RuntimeException if no database is selected or if the table does not exist.
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

            // Identify rows that match the condition
            List<Integer> rowIdsToDelete = new ArrayList<>();
            for (Row row : table.getRows()) {
                if (condition.evaluate(table, row)) {
                    rowIdsToDelete.add(row.getId());
                }
            }

            // Delete the matching rows from the table
            dbManager.deleteRows(table, rowIdsToDelete);

            return new QueryResult(); // Return an empty QueryResult to indicate success
        } catch (Exception e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }
}
