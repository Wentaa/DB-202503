// LikeCondition.java
package edu.uob.conditions;

import edu.uob.models.Row;
import edu.uob.models.Table;

public class LikeCondition extends Condition {
    private String attributeName;
    private String pattern;

    public LikeCondition(String attributeName, String pattern) {
        this.attributeName = attributeName;

        // Extract the pattern from quotes
        if (pattern.startsWith("'") && pattern.endsWith("'")) {
            this.pattern = pattern.substring(1, pattern.length() - 1);
        } else {
            this.pattern = pattern;
        }
    }

    @Override
    public boolean evaluate(Table table, Row row) {
        int columnIndex = table.getColumnIndex(attributeName);
        if (columnIndex == -1) {
            throw new RuntimeException("Column not found: " + attributeName);
        }

        String rowValue = row.getValue(columnIndex);
        if (rowValue == null) {
            return false; // NULL doesn't match any pattern
        }

        // Simple substring matching (not full SQL LIKE with % wildcards)
        return rowValue.contains(pattern);
    }
}