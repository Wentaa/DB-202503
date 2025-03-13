package edu.uob.conditions;

import edu.uob.models.Row;
import edu.uob.models.Table;

/**
 * Abstract base class for representing conditions in SQL-like queries.
 * Subclasses must implement the `evaluate` method to define their specific condition logic.
 */
public abstract class Condition {

    /**
     * Evaluates the condition on a given row of data.
     *
     * @param table The table to which the row belongs.
     * @param row   The row being evaluated.
     * @return `true` if the condition is met, otherwise `false`.
     */
    public abstract boolean evaluate(Table table, Row row);
}
