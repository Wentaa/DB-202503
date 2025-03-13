// JoinCommand.java
package edu.uob.commands;

import edu.uob.models.Column;
import edu.uob.models.Database;
import edu.uob.models.QueryResult;
import edu.uob.models.Row;
import edu.uob.models.Table;
import edu.uob.storage.DBManager;

import java.util.ArrayList;
import java.util.List;

public class JoinCommand extends Command {
    private String table1Name;
    private String table2Name;
    private String attribute1Name;
    private String attribute2Name;

    public JoinCommand(String table1Name, String table2Name, String attribute1Name, String attribute2Name) {
        this.table1Name = table1Name;
        this.table2Name = table2Name;
        this.attribute1Name = attribute1Name;
        this.attribute2Name = attribute2Name;
    }

    @Override
    public QueryResult execute(DBManager dbManager) {
        try {
            Database currentDb = dbManager.getCurrentDatabase();
            if (currentDb == null) {
                throw new RuntimeException("No database selected");
            }

            // Get tables
            Table table1 = currentDb.getTable(table1Name);
            Table table2 = currentDb.getTable(table2Name);

            if (table1 == null) {
                throw new RuntimeException("Table does not exist: " + table1Name);
            }
            if (table2 == null) {
                throw new RuntimeException("Table does not exist: " + table2Name);
            }

            // Get attribute indexes
            int attr1Index = table1.getColumnIndex(attribute1Name);
            int attr2Index = table2.getColumnIndex(attribute2Name);

            if (attr1Index == -1) {
                throw new RuntimeException("Column not found in " + table1Name + ": " + attribute1Name);
            }
            if (attr2Index == -1) {
                throw new RuntimeException("Column not found in " + table2Name + ": " + attribute2Name);
            }

            // Create joined result columns
            List<String> resultColumns = new ArrayList<>();
            resultColumns.add("id");

            // Add columns from table1 (except id)
            for (Column col : table1.getColumns()) {
                if (!col.getName().equalsIgnoreCase("id")) {
                    resultColumns.add(table1Name + "." + col.getName());
                }
            }

            // Add columns from table2 (except id)
            for (Column col : table2.getColumns()) {
                if (!col.getName().equalsIgnoreCase("id")) {
                    resultColumns.add(table2Name + "." + col.getName());
                }
            }

            QueryResult result = new QueryResult(resultColumns);

            // Perform JOIN operation
            int joinId = 1;
            for (Row row1 : table1.getRows()) {
                String value1 = row1.getValue(attr1Index);

                for (Row row2 : table2.getRows()) {
                    String value2 = row2.getValue(attr2Index);

                    // If values match, create joined row
                    if (value1 != null && value1.equals(value2)) {
                        List<String> joinedRow = new ArrayList<>();
                        joinedRow.add(String.valueOf(joinId++));

                        // Add values from table1 (except id)
                        for (int i = 0; i < row1.getValues().size(); i++) {
                            if (i != 0) { // Skip id
                                joinedRow.add(row1.getValue(i));
                            }
                        }

                        // Add values from table2 (except id)
                        for (int i = 0; i < row2.getValues().size(); i++) {
                            if (i != 0) { // Skip id
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