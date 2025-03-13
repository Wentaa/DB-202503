// NotEqualsCondition.java
package edu.uob.conditions;

import edu.uob.models.Row;
import edu.uob.models.Table;

public class NotEqualsCondition extends Condition {
    private EqualsCondition equalsCondition;

    public NotEqualsCondition(String attributeName, String value) {
        this.equalsCondition = new EqualsCondition(attributeName, value);
    }

    @Override
    public boolean evaluate(Table table, Row row) {
        return !equalsCondition.evaluate(table, row);
    }
}