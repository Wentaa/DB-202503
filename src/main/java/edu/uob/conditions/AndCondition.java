package edu.uob.conditions;

import edu.uob.models.Row;
import edu.uob.models.Table;

/**
 * Represents a logical "AND" condition used in SQL queries.
 * This condition evaluates to `true` only if both subconditions are satisfied.
 *
 * Example: `WHERE mark > 18 AND computers > 50000`
 */
public class AndCondition extends Condition {
    private Condition left;  // The left-hand condition in the AND operation
    private Condition right; // The right-hand condition in the AND operation

    /**
     * Constructs an `AND` condition with two subconditions.
     *
     * @param left  The left-hand condition.
     * @param right The right-hand condition.
     */
    public AndCondition(Condition left, Condition right) {
        this.left = left;
        this.right = right;
    }

    /**
     * Evaluates the `AND` condition on a given row.
     * The condition is true only if both the left and right conditions evaluate to true.
     *
     * @param table The table to which the row belongs.
     * @param row   The row being evaluated.
     * @return `true` if both conditions are met, otherwise `false`.
     */
    @Override
    public boolean evaluate(Table table, Row row) {
        return left.evaluate(table, row) && right.evaluate(table, row);
    }
}
