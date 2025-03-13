package edu.uob.conditions;

import edu.uob.models.Row;
import edu.uob.models.Table;

/**
 * Represents a `LIKE` condition in SQL-like queries.
 * This condition checks if a column's value contains a specified pattern.
 */
public class LikeCondition extends Condition {
    private String attributeName; // The column name to evaluate
    private String pattern;       // The pattern to match against

    /**
     * Constructs a `LIKE` condition for pattern matching.
     *
     * @param attributeName The name of the column to check.
     * @param pattern       The pattern to match against, potentially enclosed in single quotes.
     */
    public LikeCondition(String attributeName, String pattern) {
        this.attributeName = attributeName;

        // Remove surrounding single quotes if present
        if (pattern.startsWith("'") && pattern.endsWith("'")) {
            this.pattern = pattern.substring(1, pattern.length() - 1);
        } else {
            this.pattern = pattern;
        }
    }

    /**
     * Evaluates the condition on a given row.
     * Determines if the value in the specified column contains the given pattern.
     *
     * @param table The table containing the row.
     * @param row   The row being evaluated.
     * @return `true` if the column value contains the pattern, otherwise `false`.
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
            return false; // NULL doesn't match any pattern
        }

        // Simple substring matching (not full SQL LIKE with % wildcards)
        return rowValue.contains(pattern);
    }
}
