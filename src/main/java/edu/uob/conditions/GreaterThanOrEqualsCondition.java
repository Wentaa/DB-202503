// GreaterThanOrEqualsCondition.java
package edu.uob.conditions;

import edu.uob.models.Row;
import edu.uob.models.Table;

public class GreaterThanOrEqualsCondition extends Condition {
    private GreaterThanCondition greaterThanCondition;
    private EqualsCondition equalsCondition;

    public GreaterThanOrEqualsCondition(String attributeName, String value) {
        this.greaterThanCondition = new GreaterThanCondition(attributeName, value);
        this.equalsCondition = new EqualsCondition(attributeName, value);
    }

    @Override
    public boolean evaluate(Table table, Row row) {
        return greaterThanCondition.evaluate(table, row) || equalsCondition.evaluate(table, row);
    }
}