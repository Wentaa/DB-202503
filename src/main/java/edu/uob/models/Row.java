package edu.uob.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a row in a database table.
 * Each row has a unique ID and a list of values corresponding to table columns.
 */
public class Row {
    private int id;              // Unique identifier for the row
    private List<String> values; // List of values stored in the row

    /**
     * Constructs a `Row` with a specified ID and an empty list of values.
     *
     * @param id The unique identifier for the row.
     */
    public Row(int id) {
        this.id = id;
        this.values = new ArrayList<>();
    }

    /**
     * Constructs a `Row` with a specified ID and an initial list of values.
     *
     * @param id     The unique identifier for the row.
     * @param values The initial values for the row.
     */
    public Row(int id, List<String> values) {
        this.id = id;
        this.values = new ArrayList<>(values);
    }

    /**
     * Gets the ID of the row.
     *
     * @return The row's unique identifier.
     */
    public int getId() {
        return id;
    }

    /**
     * Retrieves all values stored in the row.
     *
     * @return A list of values in the row.
     */
    public List<String> getValues() {
        return values;
    }

    /**
     * Adds a new value to the row.
     *
     * @param value The value to be added.
     */
    public void addValue(String value) {
        values.add(value);
    }

    /**
     * Retrieves a value from the row based on its column index.
     *
     * @param index The index of the value to retrieve.
     * @return The value at the specified index, or `null` if the index is out of bounds.
     */
    public String getValue(int index) {
        if (index >= values.size()) {
            return null;
        }
        return values.get(index);
    }
}
