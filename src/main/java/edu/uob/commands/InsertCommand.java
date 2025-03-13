// InsertCommand.java
package edu.uob.commands;

import edu.uob.models.Database;
import edu.uob.models.QueryResult;
import edu.uob.models.Table;
import edu.uob.storage.DBManager;

import java.util.ArrayList;
import java.util.List;

public class InsertCommand extends Command {
    private String tableName;
    private List<String> values;

    public InsertCommand(String tableName, List<String> values) {
        this.tableName = tableName;
        this.values = values;
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

            // Process values, removing quotes from string literals
            List<String> processedValues = new ArrayList<>();
            for (String value : values) {
                if (value.startsWith("'") && value.endsWith("'")) {
                    processedValues.add(value.substring(1, value.length() - 1));
                } else {
                    processedValues.add(value);
                }
            }

            // Insert the row
            dbManager.insertRow(tableName, processedValues);

            QueryResult result = new QueryResult();
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }
}