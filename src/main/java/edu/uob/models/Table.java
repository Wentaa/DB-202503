package edu.uob.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a database table, storing column definitions and row data.
 * Each table has a unique name, a list of columns, and a list of rows.
 */
public class Table {
    private String name;          // The name of the table (stored in lowercase for case insensitivity)
    private List<Column> columns; // List of column definitions
    private List<Row> rows;       // List of rows containing table data
    private int nextId;           // Counter for generating unique row IDs

    /**
     * Constructs a `Table` with a given name and initializes an ID column.
     *
     * @param name The name of the table.
     */
    public Table(String name) {
        this.name = name.toLowerCase();
        this.columns = new ArrayList<>();
        this.columns.add(new Column("id", 0)); // Add ID column as the first column
        this.rows = new ArrayList<>();
        this.nextId = 1;
    }

    /**
     * Gets the name of the table.
     *
     * @return The table name.
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves all column definitions in the table.
     *
     * @return A list of `Column` objects.
     */
    public List<Column> getColumns() {
        return columns;
    }

    /**
     * Retrieves all rows in the table.
     *
     * @return A list of `Row` objects.
     */
    public List<Row> getRows() {
        return rows;
    }

    /**
     * Adds a new column to the table.
     *
     * @param columnName The name of the new column.
     * @throws IllegalArgumentException if the column already exists.
     */
    public void addColumn(String columnName) {
        for (Column col : columns) {
            if (col.getName().equalsIgnoreCase(columnName)) {
                throw new IllegalArgumentException("Column " + columnName + " already exists");
            }
        }

        columns.add(new Column(columnName, columns.size()));

        // Append null values for the new column in existing rows
        for (Row row : rows) {
            row.addValue(null);
        }
    }

    /**
     * Inserts a new row into the table.
     *
     * @param values The values for the new row (excluding the ID).
     * @throws IllegalArgumentException if the number of values doesn't match the column count.
     */
    public void addRow(List<String> values) {
        if (values.size() != columns.size() - 1) { // Exclude ID column
            throw new IllegalArgumentException("Value count doesn't match column count");
        }

        Row row = new Row(nextId);
        row.addValue(String.valueOf(nextId)); // Assign ID value

        // Add other column values
        for (String value : values) {
            row.addValue(value);
        }

        rows.add(row);
        nextId++;
    }

    /**
     * Deletes a row from the table by ID.
     *
     * @param id The ID of the row to delete.
     */
    public void deleteRow(int id) {
        rows.removeIf(row -> row.getId() == id);
    }

    /**
     * Gets the index of a column by name.
     *
     * @param columnName The name of the column.
     * @return The column index, or -1 if not found.
     */
    public int getColumnIndex(String columnName) {
        for (Column col : columns) {
            if (col.getName().equalsIgnoreCase(columnName)) {
                return col.getIndex();
            }
        }
        return -1;
    }

    /**
     * Checks if the table contains a column with the given name.
     *
     * @param columnName The column name to check.
     * @return `true` if the column exists, otherwise `false`.
     */
    public boolean hasColumn(String columnName) {
        return getColumnIndex(columnName) != -1;
    }

    /**
     * Removes a column from the table.
     *
     * @param columnName The name of the column to drop.
     * @throws IllegalArgumentException if trying to drop the ID column or if the column doesn't exist.
     */
    public void dropColumn(String columnName) {
        if (columnName.equalsIgnoreCase("id")) {
            throw new IllegalArgumentException("Cannot drop ID column");
        }

        int columnIndex = getColumnIndex(columnName);
        if (columnIndex == -1) {
            throw new IllegalArgumentException("Column " + columnName + " does not exist");
        }

        // Remove column from column list
        columns.remove(columnIndex);

        // Reassign column indexes
        for (int i = columnIndex; i < columns.size(); i++) {
            columns.set(i, new Column(columns.get(i).getName(), i));
        }

        // Remove values from all rows
        for (Row row : rows) {
            row.getValues().remove(columnIndex);
        }
    }

    /**
     * Retrieves a row by its unique ID.
     *
     * @param id The ID of the row to retrieve.
     * @return The corresponding `Row` object, or `null` if not found.
     */
    public Row getRowById(int id) {
        for (Row row : rows) {
            if (row.getId() == id) {
                return row;
            }
        }
        return null;
    }

    /**
     * Updates a row's values based on a set of assignments.
     *
     * @param rowId       The ID of the row to update.
     * @param assignments A map of column names to new values.
     * @throws IllegalArgumentException if the row doesn't exist or if any column is invalid.
     */
    public void updateRow(int rowId, Map<String, String> assignments) {
        Row row = getRowById(rowId);
        if (row == null) {
            throw new IllegalArgumentException("Row with ID " + rowId + " not found");
        }

        // Update values
        for (Map.Entry<String, String> entry : assignments.entrySet()) {
            String columnName = entry.getKey();
            String value = entry.getValue();

            int columnIndex = getColumnIndex(columnName);
            if (columnIndex == -1) {
                throw new IllegalArgumentException("Column " + columnName + " not found");
            }

            // Remove surrounding single quotes if value is a string
            if (value.startsWith("'") && value.endsWith("'")) {
                value = value.substring(1, value.length() - 1);
            }

            row.getValues().set(columnIndex, value);
        }
    }

    /**
     * Gets the next available ID for row insertion.
     *
     * @return The next available row ID.
     */
    public int getNextId() {
        return nextId;
    }

    /**
     * Sets the next available ID for row insertion.
     *
     * @param nextId The next available row ID.
     */
    public void setNextId(int nextId) {
        this.nextId = nextId;
    }
}
