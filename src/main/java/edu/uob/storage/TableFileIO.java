// TableFileIO.java
package edu.uob.storage;

import edu.uob.models.Column;
import edu.uob.models.Row;
import edu.uob.models.Table;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TableFileIO {

    public static void saveTable(Table table, String dbPath) throws IOException {
        File tableFile = new File(dbPath + File.separator + table.getName() + ".tab");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tableFile))) {
            // 写入列名
            List<Column> columns = table.getColumns();
            for (int i = 0; i < columns.size(); i++) {
                writer.write(columns.get(i).getName());
                if (i < columns.size() - 1) {
                    writer.write("\t");
                }
            }
            writer.newLine();

            // 写入数据行
            for (Row row : table.getRows()) {
                List<String> values = row.getValues();
                for (int i = 0; i < values.size(); i++) {
                    String value = values.get(i);
                    writer.write(value != null ? value : "NULL");
                    if (i < values.size() - 1) {
                        writer.write("\t");
                    }
                }
                writer.newLine();
            }
        }
    }

    public static Table loadTable(String tableName, String dbPath) throws IOException {
        File tableFile = new File(dbPath + File.separator + tableName + ".tab");
        if (!tableFile.exists()) {
            throw new IOException("Table file not found: " + tableName);
        }

        Table table = new Table(tableName);

        try (BufferedReader reader = new BufferedReader(new FileReader(tableFile))) {
            // Read column names
            String headerLine = reader.readLine();
            if (headerLine == null) {
                throw new IOException("Empty table file: " + tableName);
            }

            String[] columnNames = headerLine.split("\t");

            // Clear the default columns created by the table constructor
            table.getColumns().clear();

            // Add columns from file
            for (int i = 0; i < columnNames.length; i++) {
                table.getColumns().add(new Column(columnNames[i], i));
            }

            // Read data rows
            String line;
            int maxId = 0;

            while ((line = reader.readLine()) != null) {
                String[] values = line.split("\t");

                // Parse ID
                int id = Integer.parseInt(values[0]);
                if (id > maxId) {
                    maxId = id;
                }

                // Create row
                Row row = new Row(id);
                for (String value : values) {
                    row.addValue(value);
                }

                table.getRows().add(row);
            }

            // Update table's nextId to be one more than the maximum ID
            table.setNextId(maxId + 1);
        }

        return table;
    }
}