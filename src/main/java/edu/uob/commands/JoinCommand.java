package edu.uob.commands;

import edu.uob.models.Column;
import edu.uob.models.Database;
import edu.uob.models.QueryResult;
import edu.uob.models.Row;
import edu.uob.models.Table;
import edu.uob.storage.DBManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles the SQL `JOIN` command, performing an inner join between two tables
 * based on a specified column match.
 */
public class JoinCommand extends Command {
    private String table1Name;     // Name of the first table in the join
    private String table2Name;     // Name of the second table in the join
    private String attribute1Name; // Column from table1 used for joining
    private String attribute2Name; // Column from table2 used for joining

    /**
     * Constructs a `JOIN` command.
     *
     * @param table1Name     The name of the first table.
     * @param table2Name     The name of the second table.
     * @param attribute1Name The column from the first table used as a join key.
     * @param attribute2Name The column from the second table used as a join key.
     */
    public JoinCommand(String table1Name, String table2Name, String attribute1Name, String attribute2Name) {
        this.table1Name = table1Name;
        this.table2Name = table2Name;
        this.attribute1Name = attribute1Name;
        this.attribute2Name = attribute2Name;
    }

    /**
     * Executes the `JOIN` command.
     * It performs an inner join between two tables based on matching values in the specified columns.
     *
     * @param dbManager The database manager that provides access to the tables.
     * @return A `QueryResult` containing the joined data.
     * @throws RuntimeException if no database is selected, a table does not exist,
     *                          or the specified columns are not found.
     */
    @Override
    public QueryResult execute(DBManager dbManager) {
        try {
            Database currentDb = dbManager.getCurrentDatabase();
            if (currentDb == null) {
                throw new RuntimeException("No database selected");
            }

            // Retrieve tables
            Table table1 = currentDb.getTable(table1Name);
            Table table2 = currentDb.getTable(table2Name);

            if (table1 == null) {
                throw new RuntimeException("Table does not exist: " + table1Name);
            }
            if (table2 == null) {
                throw new RuntimeException("Table does not exist: " + table2Name);
            }

            // Get the index of the join attributes in each table
            int attr1Index = table1.getColumnIndex(attribute1Name);
            int attr2Index = table2.getColumnIndex(attribute2Name);

            if (attr1Index == -1) {
                throw new RuntimeException("Column not found in " + table1Name + ": " + attribute1Name);
            }
            if (attr2Index == -1) {
                throw new RuntimeException("Column not found in " + table2Name + ": " + attribute2Name);
            }

            // Construct the column headers for the joined table
            List<String> resultColumns = new ArrayList<>();
            resultColumns.add("id"); // New ID for joined rows

            // Add columns from table1 (excluding its original ID column)
            for (Column col : table1.getColumns()) {
                if (!col.getName().equalsIgnoreCase("id")) {
                    resultColumns.add(table1Name + "." + col.getName());
                }
            }

            // Add columns from table2 (excluding its original ID column)
            for (Column col : table2.getColumns()) {
                if (!col.getName().equalsIgnoreCase("id")) {
                    resultColumns.add(table2Name + "." + col.getName());
                }
            }

            QueryResult result = new QueryResult(resultColumns);

            // Perform the join operation
            int joinId = 1;
            for (Row row1 : table1.getRows()) {
                String value1 = row1.getValue(attr1Index);

                for (Row row2 : table2.getRows()) {
                    String value2 = row2.getValue(attr2Index);

                    // If the join attributes match, create a new joined row
                    if (value1 != null && value1.equals(value2)) {
                        List<String> joinedRow = new ArrayList<>();
                        joinedRow.add(String.valueOf(joinId++));

                        // Add values from table1 (excluding the ID column)
                        for (int i = 0; i < row1.getValues().size(); i++) {
                            if (i != 0) { // Skip the ID column
                                joinedRow.add(row1.getValue(i));
                            }
                        }

                        // Add values from table2 (excluding the ID column)
                        for (int i = 0; i < row2.getValues().size(); i++) {
                            if (i != 0) { // Skip the ID column
                                joinedRow.add(row2.getValue(i));
                            }
                        }

                        result.addRow(joinedRow);
                    }
                }
            }

            return result;
        } catch (Exception e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }
}
