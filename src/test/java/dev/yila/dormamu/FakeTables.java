package dev.yila.dormamu;

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
    public State getState() {
        Map<String, List<Row>> state = new HashMap<>();
        this.tables.forEach((name, rows) -> state.put(name, new ArrayList<>(rows)));
        return new State(state);
    }

    public FakeTables deleteRow(String table, Row row) {
        this.tables.get(table).remove(row);
        return this;
    }

    public FakeTables updateRow(String table, String id, String columnName, String columnValue) {
        Row newRow = this.tables.get(table)
                .stream().filter(row -> row.getId().equals(id))
                .findFirst()
                .map(row -> {
                    this.tables.get(table).remove(row);
                    return new FakeRow(row.getId(), columnName, columnValue);
                }).orElseThrow(() -> new RuntimeException("Not found row to update with id " + id));
        return insertRow(table, newRow);
    }
}
