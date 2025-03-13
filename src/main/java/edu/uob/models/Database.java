// Database.java
package edu.uob.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Database {
    private String name;
    private Map<String, Table> tables;

    public Database(String name) {
        this.name = name.toLowerCase();
        this.tables = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public Map<String, Table> getTables() {
        return tables;
    }

    public void addTable(Table table) {
        tables.put(table.getName().toLowerCase(), table);
    }

    public Table getTable(String tableName) {
        return tables.get(tableName.toLowerCase());
    }

    public void dropTable(String tableName) {
        tables.remove(tableName.toLowerCase());
    }

    public boolean hasTable(String tableName) {
        return tables.containsKey(tableName.toLowerCase());
    }

    // Add this method to fix the error
    public void createTable(String tableName, List<String> columnNames) {
        Table table = new Table(tableName);
        for (String columnName : columnNames) {
            table.addColumn(columnName);
        }
        tables.put(tableName.toLowerCase(), table);
    }
}