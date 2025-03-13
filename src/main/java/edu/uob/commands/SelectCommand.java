package edu.uob.commands;

import edu.uob.conditions.Condition;
import edu.uob.models.Column;
import edu.uob.models.Database;
import edu.uob.models.QueryResult;
import edu.uob.models.Row;
import edu.uob.models.Table;
import edu.uob.storage.DBManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles the SQL `SELECT` command for retrieving data from a table.
 * Supports selecting specific columns or all columns (`SELECT *`),
 * and applying an optional condition to filter rows.
 */
public class SelectCommand extends Command {
    private String tableName;       // Name of the table to query
    private List<String> attributes; // List of attributes to select (or "*" for all)
    private Condition condition;     // Optional condition for filtering rows

    /**
     * Constructs a `SELECT` command.
     *
     * @param tableName  The name of the table to query.
     * @param attributes The list of column names to retrieve (or "*" for all columns).
     * @param condition  An optional condition to filter the selected rows.
     */
    public SelectCommand(String tableName, List<String> attributes, Condition condition) {
        this.tableName = tableName;
        this.attributes = attributes;
        this.condition = condition;
    }

    /**
     * Executes the `SELECT` command.
     * Retrieves data from the specified table, filters it based on the condition (if provided),
     * and returns the matching rows.
     *
     * @param dbManager The database manager handling the operation.
     * @return A `QueryResult` containing the selected data.
     * @throws RuntimeException if no database is selected, the table does not exist,
     *                          or if a requested column does not exist.
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

            // Determine which columns to return
            List<Column> selectedColumns = new ArrayList<>();
            List<Integer> columnIndexes = new ArrayList<>();

            if (attributes.size() == 1 && attributes.get(0).equals("*")) {
                // Select all columns
                selectedColumns.addAll(table.getColumns());
                for (int i = 0; i < table.getColumns().size(); i++) {
                    columnIndexes.add(i);
                }
            } else {
                // Select specific columns
                for (String attrName : attributes) {
                    int columnIndex = table.getColumnIndex(attrName);
                    if (columnIndex == -1) {
                        throw new RuntimeException("Column not found: " + attrName);
                    }
                    selectedColumns.add(table.getColumns().get(columnIndex));
                    columnIndexes.add(columnIndex);
                }
            }

            // Prepare result with column headers
            List<String> columnNames = new ArrayList<>();
            for (Column col : selectedColumns) {
                columnNames.add(col.getName());
            }
            QueryResult result = new QueryResult(columnNames);

            // Iterate through rows and filter based on the condition
            for (Row row : table.getRows()) {
                if (condition == null || condition.evaluate(table, row)) {
                    List<String> resultRow = new ArrayList<>();
                    for (int colIndex : columnIndexes) {
                        resultRow.add(row.getValue(colIndex));
                    }
                    result.addRow(resultRow);
                }
            }

            return result;
        } catch (Exception e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }
}
