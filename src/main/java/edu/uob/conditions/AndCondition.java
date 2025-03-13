// AndCondition.java
package edu.uob.conditions;

import edu.uob.models.Row;
import edu.uob.models.Table;

public class AndCondition extends Condition {
    private Condition left;
    private Condition right;

    public AndCondition(Condition left, Condition right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean evaluate(Table table, Row row) {
        return left.evaluate(table, row) && right.evaluate(table, row);
    }
}