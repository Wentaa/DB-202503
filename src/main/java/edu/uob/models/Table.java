// Table.java - with additional methods needed
package edu.uob.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Table {
    private String name;
    private List<Column> columns;
    private List<Row> rows;
    private int nextId;

    public Table(String name) {
        this.name = name.toLowerCase();
        this.columns = new ArrayList<>();
        // Add ID column as first column
        this.columns.add(new Column("id", 0));
        this.rows = new ArrayList<>();
        this.nextId = 1;
    }

    public String getName() {
        return name;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public List<Row> getRows() {
        return rows;
    }

    public void addColumn(String columnName) {
        // Check if column name already exists
        for (Column col : columns) {
            if (col.getName().equalsIgnoreCase(columnName)) {
                throw new IllegalArgumentException("Column " + columnName + " already exists");
            }
        }

        columns.add(new Column(columnName, columns.size()));

        // Add null values for new column in existing rows
        for (Row row : rows) {
            row.addValue(null);
        }
    }

    public void addRow(List<String> values) {
        if (values.size() != columns.size() - 1) { // -1 for ID column
            throw new IllegalArgumentException("Value count doesn't match column count");
        }

        Row row = new Row(nextId);
        // Add ID value
        row.addValue(String.valueOf(nextId));
        // Add all other values
        for (String value : values) {
            row.addValue(value);
        }

        rows.add(row);
        nextId++;
    }

    public void deleteRow(int id) {
        for (int i = 0; i < rows.size(); i++) {
            if (rows.get(i).getId() == id) {
                rows.remove(i);
                return;
            }
        }
    }

    public int getColumnIndex(String columnName) {
        for (Column col : columns) {
            if (col.getName().equalsIgnoreCase(columnName)) {
                return col.getIndex();
            }
        }
        return -1;
    }

    public boolean hasColumn(String columnName) {
        return getColumnIndex(columnName) != -1;
    }

    public void dropColumn(String columnName) {
        if (columnName.equalsIgnoreCase("id")) {
            throw new IllegalArgumentException("Cannot drop ID column");
        }

        int columnIndex = getColumnIndex(columnName);
        if (columnIndex == -1) {
            throw new IllegalArgumentException("Column " + columnName + " does not exist");
        }

        // Remove column
        columns.remove(columnIndex);

        // Recalculate column indexes
        for (int i = columnIndex; i < columns.size(); i++) {
            columns.set(i, new Column(columns.get(i).getName(), i));
        }

        // Remove column values from all rows
        for (Row row : rows) {
            List<String> values = row.getValues();
            values.remove(columnIndex);
        }
    }

    public Row getRowById(int id) {
        for (Row row : rows) {
            if (row.getId() == id) {
                return row;
            }
        }
        return null;
    }

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

            // Process value
            if (value.startsWith("'") && value.endsWith("'")) {
                value = value.substring(1, value.length() - 1);
            }

            row.getValues().set(columnIndex, value);
        }
    }

    public void setNextId(int nextId) {
        this.nextId = nextId;
    }

    public int getNextId() {
        return nextId;
    }
}