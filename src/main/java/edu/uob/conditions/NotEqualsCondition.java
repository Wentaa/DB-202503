package edu.uob.conditions;

import edu.uob.models.Row;
import edu.uob.models.Table;

/**
 * Represents a `!=` (not equals) condition in SQL-like queries.
 * This condition evaluates to `true` if the column value is NOT equal to the specified value.
 */
public class NotEqualsCondition extends Condition {
    private EqualsCondition equalsCondition; // Reuses EqualsCondition to perform the inverse comparison

    /**
     * Constructs a `NotEqualsCondition` by inverting an `EqualsCondition`.
     *
     * @param attributeName The name of the column to check.
     * @param value         The value to compare against.
     */
    public NotEqualsCondition(String attributeName, String value) {
        this.equalsCondition = new EqualsCondition(attributeName, value);
    }

    /**
     * Evaluates the condition on a given row.
     * Determines if the value in the specified column is NOT equal to the provided value.
     *
     * @param table The table containing the row.
     * @param row   The row being evaluated.
     * @return `true` if the column value is NOT equal to the target value, otherwise `false`.
     */
    @Override
    public boolean evaluate(Table table, Row row) {
        return !equalsCondition.evaluate(table, row);
    }
}
