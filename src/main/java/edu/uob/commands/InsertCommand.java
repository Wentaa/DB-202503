package edu.uob.commands;

import edu.uob.models.Database;
import edu.uob.models.QueryResult;
import edu.uob.models.Table;
import edu.uob.storage.DBManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles the SQL `INSERT` command for adding new rows to a table.
 */
public class InsertCommand extends Command {
    private String tableName;   // Name of the table where the row will be inserted
    private List<String> values; // List of values to be inserted

    /**
     * Constructs an `INSERT` command.
     *
     * @param tableName The name of the target table.
     * @param values    The list of values to insert into the table.
     */
    public InsertCommand(String tableName, List<String> values) {
        this.tableName = tableName;
        this.values = values;
    }

    /**
     * Executes the `INSERT` command.
     * It processes the values and inserts a new row into the specified table.
     *
     * @param dbManager The database manager handling the operation.
     * @return A `QueryResult` indicating the success of the operation.
     * @throws RuntimeException if no database is selected, the table does not exist,
     *                          or an error occurs during insertion.
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

            // Process values by removing surrounding quotes from string literals
            List<String> processedValues = new ArrayList<>();
            for (String value : values) {
                if (value.startsWith("'") && value.endsWith("'")) {
                    processedValues.add(value.substring(1, value.length() - 1));
                } else {
                    processedValues.add(value);
                }
            }

            // Insert the processed values as a new row in the table
            dbManager.insertRow(tableName, processedValues);

            return new QueryResult(); // Return an empty QueryResult to indicate success
        } catch (Exception e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }
}
