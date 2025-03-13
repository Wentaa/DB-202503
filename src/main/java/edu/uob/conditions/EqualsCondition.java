package edu.uob.conditions;

import edu.uob.models.Row;
import edu.uob.models.Table;

/**
 * Represents an equality condition (`=`) in SQL-like queries.
 * Compares a specified column's value in a row to a given target value.
 */
public class EqualsCondition extends Condition {
    private String attributeName; // The column name to evaluate
    private String value;         // The value to compare against

    /**
     * Constructs an `EqualsCondition` to compare a column value with a specified value.
     *
     * @param attributeName The name of the column to check.
     * @param value         The value to compare against.
     */
    public EqualsCondition(String attributeName, String value) {
        this.attributeName = attributeName;
        this.value = value;
    }

    /**
     * Evaluates the condition on a given row.
     * Determines if the value in the specified column equals the provided value.
     *
     * @param table The table containing the row.
     * @param row   The row being evaluated.
     * @return `true` if the column value matches the expected value, otherwise `false`.
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
            return value.equals("NULL");
        }

        // Handle different value types
        if (value.equals("NULL")) {
            return rowValue == null;
        } else if (value.startsWith("'") && value.endsWith("'")) {
            // String comparison (removes surrounding single quotes)
            String stringValue = value.substring(1, value.length() - 1);
            return rowValue.equals(stringValue);
        } else if (value.equals("TRUE") || value.equals("FALSE")) {
            // Boolean comparison
            return rowValue.equalsIgnoreCase(value);
        } else {
            // Numeric comparison (handles both integers and floats)
            try {
                if (rowValue.contains(".") || value.contains(".")) {
                    // Float comparison
                    float rowFloat = Float.parseFloat(rowValue);
                    float valueFloat = Float.parseFloat(value);
                    return rowFloat == valueFloat;
                } else {
                    // Integer comparison
                    int rowInt = Integer.parseInt(rowValue);
                    int valueInt = Integer.parseInt(value);
                    return rowInt == valueInt;
                }
            } catch (NumberFormatException e) {
                // If not numeric, fallback to string comparison
                return rowValue.equals(value);
            }
        }
    }
}
