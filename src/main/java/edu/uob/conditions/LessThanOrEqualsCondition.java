// LessThanOrEqualsCondition.java
package edu.uob.conditions;

import edu.uob.models.Row;
import edu.uob.models.Table;

public class LessThanOrEqualsCondition extends Condition {
    private LessThanCondition lessThanCondition;
    private EqualsCondition equalsCondition;

    public LessThanOrEqualsCondition(String attributeName, String value) {
        this.lessThanCondition = new LessThanCondition(attributeName, value);
        this.equalsCondition = new EqualsCondition(attributeName, value);
    }

    @Override
    public boolean evaluate(Table table, Row row) {
        return lessThanCondition.evaluate(table, row) || equalsCondition.evaluate(table, row);
    }
}