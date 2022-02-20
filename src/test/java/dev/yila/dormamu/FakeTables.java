package dev.yila.dormamu;

import dev.yila.dormamu.test.Tables;

import java.util.*;

public class FakeTables implements Tables {

    public static final String TABLE_ONE = "tableOne";

    private final Map<String, List<Row>> tables;

    public FakeTables() {
        this.tables = new HashMap<>();
        this.tables.put(TABLE_ONE, new ArrayList<>());
    }

    public FakeTables insertRow(String table, Row row) {
        this.tables.compute(table, (name, rows) -> {
            if (rows == null) {
                return List.of(row);
            }
            rows.add(row);
            return rows;
        });
        return this;
    }

    @Override
    public DbState getState() {
        Map<String, List<Row>> state = new HashMap<>();
        this.tables.forEach((name, rows) -> state.put(name, new ArrayList<>(rows)));
        return new DbState(state);
    }

    public void deleteRow(String table, Row row) {
        this.tables.get(table).remove(row);
    }
}
