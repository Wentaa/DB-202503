package edu.uob.conditions;

import edu.uob.models.Row;
import edu.uob.models.Table;

/**
 * Represents a `>=` (greater than or equal to) condition in SQL-like queries.
 * This condition evaluates to `true` if the column value is greater than or equal to the specified value.
 */
public class GreaterThanOrEqualsCondition extends Condition {
    private GreaterThanCondition greaterThanCondition; // Handles "greater than" logic
    private EqualsCondition equalsCondition;           // Handles "equals" logic

    /**
     * Constructs a `GreaterThanOrEqualsCondition` by combining `>` and `=` conditions.
     *
     * @param attributeName The name of the column to check.
     * @param value         The value to compare against.
     */
    public GreaterThanOrEqualsCondition(String attributeName, String value) {
        this.greaterThanCondition = new GreaterThanCondition(attributeName, value);
        this.equalsCondition = new EqualsCondition(attributeName, value);
    }

    /**
     * Evaluates the condition on a given row.
     * Determines if the value in the specified column is greater than or equal to the provided value.
     *
     * @param table The table containing the row.
     * @param row   The row being evaluated.
     * @return `true` if the column value is greater than or equal to the target value, otherwise `false`.
     */
    @Override
    public boolean evaluate(Table table, Row row) {
        return greaterThanCondition.evaluate(table, row) || equalsCondition.evaluate(table, row);
    }
}
