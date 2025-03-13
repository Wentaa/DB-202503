package edu.uob.commands;

import edu.uob.models.Database;
import edu.uob.models.QueryResult;
import edu.uob.models.Table;
import edu.uob.storage.DBManager;

/**
 * Handles the SQL `ALTER TABLE` command.
 * This allows adding or dropping a column from an existing table.
 */
public class AlterCommand extends Command {
    private String tableName;  // Name of the table to be altered
    private String columnName; // Name of the column to be added or dropped
    private boolean isAdd;     // True if adding a column, false if dropping

    /**
     * Constructs an `ALTER TABLE` command.
     *
     * @param tableName  The name of the target table.
     * @param columnName The name of the column to add or drop.
     * @param isAdd      True to add a column, false to drop a column.
     */
    public AlterCommand(String tableName, String columnName, boolean isAdd) {
        this.tableName = tableName;
        this.columnName = columnName;
        this.isAdd = isAdd;
    }

    /**
     * Executes the `ALTER TABLE` command.
     * It either adds a new column or removes an existing one, while ensuring constraints are met.
     *
     * @param dbManager The database manager that provides access to the current database.
     * @return A QueryResult indicating the success of the operation.
     * @throws RuntimeException if the database is not selected, the table does not exist,
     *                          or if an invalid operation is attempted (e.g., removing the ID column).
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

            if (isAdd) {
                // Ensure the column does not already exist
                if (table.hasColumn(columnName)) {
                    throw new RuntimeException("Column already exists: " + columnName);
                }
                table.addColumn(columnName);
            } else {
                // Prevent dropping the primary ID column
                if (columnName.equalsIgnoreCase("id")) {
                    throw new RuntimeException("Cannot drop ID column");
                }

                // Ensure the column exists before attempting to drop it
                if (!table.hasColumn(columnName)) {
                    throw new RuntimeException("Column does not exist: " + columnName);
                }
                table.dropColumn(columnName);
            }

            // Persist the updated table data
            String dbPath = dbManager.getDatabasePath(currentDb.getName());
            dbManager.saveTable(table, dbPath);

            return new QueryResult();  // Return an empty QueryResult to indicate success
        } catch (Exception e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }
}
