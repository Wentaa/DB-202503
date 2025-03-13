// Condition.java
package edu.uob.conditions;

import edu.uob.models.Row;
import edu.uob.models.Table;

public abstract class Condition {
    public abstract boolean evaluate(Table table, Row row);
}