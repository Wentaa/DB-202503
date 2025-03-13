// QueryResult.java
package edu.uob.models;

import java.util.ArrayList;
import java.util.List;

public class QueryResult {
    private List<String> columnNames;
    private List<List<String>> rows;

    public QueryResult() {
        this.columnNames = new ArrayList<>();
        this.rows = new ArrayList<>();
    }

    public QueryResult(List<String> columnNames) {
        this.columnNames = columnNames;
        this.rows = new ArrayList<>();
    }

    public void addRow(List<String> row) {
        rows.add(row);
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    public List<List<String>> getRows() {
        return rows;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        // Add column names
        for (String colName : columnNames) {
            result.append(colName).append("\t");
        }
        result.append("\n");

        // Add data rows
        for (List<String> row : rows) {
            for (String value : row) {
                result.append(value != null ? value : "NULL").append("\t");
            }
            result.append("\n");
        }

        return result.toString();
    }
}