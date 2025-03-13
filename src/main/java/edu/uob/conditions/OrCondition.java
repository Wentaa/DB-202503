// OrCondition.java
package edu.uob.conditions;

import edu.uob.models.Row;
import edu.uob.models.Table;

public class OrCondition extends Condition {
    private Condition left;
    private Condition right;

    public OrCondition(Condition left, Condition right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean evaluate(Table table, Row row) {
        return left.evaluate(table, row) || right.evaluate(table, row);
    }
}