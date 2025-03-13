package edu.uob.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the result of a database query, including column names and row data.
 * Stores query results in a structured format for easy retrieval and display.
 */
public class QueryResult {
    private List<String> columnNames; // List of column names in the result set
    private List<List<String>> rows;  // List of rows, each containing a list of values

    /**
     * Constructs an empty `QueryResult` with no predefined column names.
     */
    public QueryResult() {
        this.columnNames = new ArrayList<>();
        this.rows = new ArrayList<>();
    }

    /**
     * Constructs a `QueryResult` with a specified list of column names.
     *
     * @param columnNames The names of the columns in the query result.
     */
    public QueryResult(List<String> columnNames) {
        this.columnNames = columnNames;
        this.rows = new ArrayList<>();
    }

    /**
     * Adds a new row to the result set.
     *
     * @param row A list of values representing a row in the result set.
     */
    public void addRow(List<String> row) {
        rows.add(row);
    }

    /**
     * Retrieves the column names in the query result.
     *
     * @return A list of column names.
     */
    public List<String> getColumnNames() {
        return columnNames;
    }

    /**
     * Retrieves all rows in the query result.
     *
     * @return A list of rows, where each row is represented as a list of values.
     */
    public List<List<String>> getRows() {
        return rows;
    }

    /**
     * Converts the query result into a formatted string representation.
     * The output includes column names followed by the row data, separated by tabs.
     *
     * @return A string representation of the query result.
     */
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
