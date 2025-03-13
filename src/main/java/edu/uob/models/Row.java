// Row.java
package edu.uob.models;

import java.util.ArrayList;
import java.util.List;

public class Row {
    private int id;
    private List<String> values;

    public Row(int id) {
        this.id = id;
        this.values = new ArrayList<>();
    }

    public Row(int id, List<String> values) {
        this.id = id;
        this.values = new ArrayList<>(values);
    }

    public int getId() {
        return id;
    }

    public List<String> getValues() {
        return values;
    }

    public void addValue(String value) {
        values.add(value);
    }

    public String getValue(int index) {
        if (index >= values.size()) {
            return null;
        }
        return values.get(index);
    }
}