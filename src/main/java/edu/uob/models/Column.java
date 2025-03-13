// Column.java
package edu.uob.models;

public class Column {
    private String name;
    private int index;

    public Column(String name, int index) {
        this.name = name;
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Column column = (Column) obj;
        return name.equalsIgnoreCase(column.name);
    }

    @Override
    public int hashCode() {
        return name.toLowerCase().hashCode();
    }
}