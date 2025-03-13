package edu.uob.conditions;

import edu.uob.models.Row;
import edu.uob.models.Table;

/**
 * Represents a `>` (greater than) condition in SQL-like queries.
 * Compares a specified column's value in a row to a given target value.
 */
public class GreaterThanCondition extends Condition {
    private String attributeName; // The column name to evaluate
    private String value;         // The value to compare against

    /**
     * Constructs a `GreaterThanCondition` to compare a column value with a specified value.
     *
     * @param attributeName The name of the column to check.
     * @param value         The value to compare against.
     */
    public GreaterThanCondition(String attributeName, String value) {
        this.attributeName = attributeName;
        this.value = value;
    }

    /**
     * Evaluates the condition on a given row.
     * Determines if the value in the specified column is greater than the provided value.
     *
     * @param table The table containing the row.
     * @param row   The row being evaluated.
     * @return `true` if the column value is greater, otherwise `false`.
     * @throws RuntimeException if the specified column does not exist.
     */
    @Override
    public boolean evaluate(Table table, Row row) {
        int columnIndex = table.getColumnIndex(attributeName);
        if (columnIndex == -1) {
            throw new RuntimeException("Column not found: " + attributeName);
        }

        String rowValue = row.getValue(columnIndex);
        if (rowValue == null) {
            return false; // NULL is not greater than anything
        }

        if (value.equals("NULL")) {
            return false; // Nothing is greater than NULL
        } else if (value.startsWith("'") && value.endsWith("'")) {
            // String comparison (removes surrounding single quotes)
            String stringValue = value.substring(1, value.length() - 1);
            return rowValue.compareTo(stringValue) > 0;
        } else if (value.equals("TRUE") || value.equals("FALSE")) {
            // Boolean comparison - TRUE is greater than FALSE
            return rowValue.equalsIgnoreCase("TRUE") && value.equals("FALSE");
        } else {
            // Numeric comparison (handles both integers and floats)
            try {
                if (rowValue.contains(".") || value.contains(".")) {
                    // Float comparison
                    float rowFloat = Float.parseFloat(rowValue);
                    float valueFloat = Float.parseFloat(value);
                    return rowFloat > valueFloat;
                } else {
                    // Integer comparison
                    int rowInt = Integer.parseInt(rowValue);
                    int valueInt = Integer.parseInt(value);
                    return rowInt > valueInt;
                }
            } catch (NumberFormatException e) {
                // If not numeric, fallback to string comparison
                return rowValue.compareTo(value) > 0;
            }
        }
    }
}
