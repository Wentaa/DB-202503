package edu.uob.models;

/**
 * Represents a column in a database table.
 * Each column has a name and an index indicating its position in the table.
 */
public class Column {
    private String name; // The name of the column
    private int index;   // The index of the column in the table

    /**
     * Constructs a `Column` with a given name and index.
     *
     * @param name  The name of the column.
     * @param index The index position of the column in the table.
     */
    public Column(String name, int index) {
        this.name = name;
        this.index = index;
    }

    /**
     * Gets the name of the column.
     *
     * @return The column name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the index position of the column in the table.
     *
     * @return The column index.
     */
    public int getIndex() {
        return index;
    }

    /**
     * Checks if this column is equal to another object.
     * Two columns are considered equal if they have the same name (case-insensitive).
     *
     * @param obj The object to compare.
     * @return `true` if the columns have the same name, otherwise `false`.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Column column = (Column) obj;
        return name.equalsIgnoreCase(column.name);
    }

    /**
     * Computes the hash code of the column based on its name (case-insensitive).
     *
     * @return The hash code of the column.
     */
    @Override
    public int hashCode() {
        return name.toLowerCase().hashCode();
    }
}
