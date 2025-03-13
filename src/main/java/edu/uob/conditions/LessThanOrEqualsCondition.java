package edu.uob.conditions;

import edu.uob.models.Row;
import edu.uob.models.Table;

/**
 * Represents a `<=` (less than or equal to) condition in SQL-like queries.
 * This condition evaluates to `true` if the column value is less than or equal to the specified value.
 */
public class LessThanOrEqualsCondition extends Condition {
    private LessThanCondition lessThanCondition; // Handles "less than" logic
    private EqualsCondition equalsCondition;     // Handles "equals" logic

    /**
     * Constructs a `LessThanOrEqualsCondition` by combining `<` and `=` conditions.
     *
     * @param attributeName The name of the column to check.
     * @param value         The value to compare against.
     */
    public LessThanOrEqualsCondition(String attributeName, String value) {
        this.lessThanCondition = new LessThanCondition(attributeName, value);
        this.equalsCondition = new EqualsCondition(attributeName, value);
    }

    /**
     * Evaluates the condition on a given row.
     * Determines if the value in the specified column is less than or equal to the provided value.
     *
     * @param table The table containing the row.
     * @param row   The row being evaluated.
     * @return `true` if the column value is less than or equal to the target value, otherwise `false`.
     */
    @Override
    public boolean evaluate(Table table, Row row) {
        return lessThanCondition.evaluate(table, row) || equalsCondition.evaluate(table, row);
    }
}
