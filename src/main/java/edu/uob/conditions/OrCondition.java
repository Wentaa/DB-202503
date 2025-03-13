package edu.uob.conditions;

import edu.uob.models.Row;
import edu.uob.models.Table;

/**
 * Represents a logical "OR" condition in SQL-like queries.
 * This condition evaluates to `true` if at least one of the subconditions is satisfied.
 *
 * Example: `WHERE age < 18 OR salary > 50000`
 */
public class OrCondition extends Condition {
    private Condition left;  // The left-hand condition in the OR operation
    private Condition right; // The right-hand condition in the OR operation

    /**
     * Constructs an `OR` condition with two subconditions.
     *
     * @param left  The left-hand condition.
     * @param right The right-hand condition.
     */
    public OrCondition(Condition left, Condition right) {
        this.left = left;
        this.right = right;
    }

    /**
     * Evaluates the `OR` condition on a given row.
     * The condition is `true` if at least one of the subconditions evaluates to `true`.
     *
     * @param table The table to which the row belongs.
     * @param row   The row being evaluated.
     * @return `true` if at least one condition is met, otherwise `false`.
     */
    @Override
    public boolean evaluate(Table table, Row row) {
        return left.evaluate(table, row) || right.evaluate(table, row);
    }
}
