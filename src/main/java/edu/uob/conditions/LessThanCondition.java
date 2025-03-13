// LessThanCondition.java
package edu.uob.conditions;

import edu.uob.models.Row;
import edu.uob.models.Table;

public class LessThanCondition extends Condition {
    private String attributeName;
    private String value;

    public LessThanCondition(String attributeName, String value) {
        this.attributeName = attributeName;
        this.value = value;
    }

    @Override
    public boolean evaluate(Table table, Row row) {
        int columnIndex = table.getColumnIndex(attributeName);
        if (columnIndex == -1) {
            throw new RuntimeException("Column not found: " + attributeName);
        }

        String rowValue = row.getValue(columnIndex);
        if (rowValue == null) {
            return false; // NULL is not less than anything
        }

        if (value.equals("NULL")) {
            return false; // Nothing is less than NULL
        } else if (value.startsWith("'") && value.endsWith("'")) {
            // String comparison
            String stringValue = value.substring(1, value.length() - 1);
            return rowValue.compareTo(stringValue) < 0;
        } else if (value.equals("TRUE") || value.equals("FALSE")) {
            // Boolean comparison - FALSE < TRUE
            return rowValue.equalsIgnoreCase("FALSE") && value.equals("TRUE");
        } else {
            // Numeric comparison
            try {
                if (rowValue.contains(".") || value.contains(".")) {
                    // Float comparison
                    float rowFloat = Float.parseFloat(rowValue);
                    float valueFloat = Float.parseFloat(value);
                    return rowFloat < valueFloat;
                } else {
                    // Integer comparison
                    int rowInt = Integer.parseInt(rowValue);
                    int valueInt = Integer.parseInt(value);
                    return rowInt < valueInt;
                }
            } catch (NumberFormatException e) {
                // If not numeric, fall back to string comparison
                return rowValue.compareTo(value) < 0;
            }
        }
    }
}