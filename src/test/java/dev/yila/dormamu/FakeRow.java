package dev.yila.dormamu;

import java.util.Optional;

public class FakeRow implements Row {

    private final String id;
    private final String columnName;
    private final String columnValue;

    public FakeRow(String id, String columnName, String columnValue) {
        this.id = id;
        this.columnName = columnName;
        this.columnValue = columnValue;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean equalColumns(Row row) {
        return row.value(this.columnName, String.class)
                .map(value -> value.equals(this.columnValue))
                .orElse(false);
    }

    @Override
    public String string(String columnName) {
        if (columnName.equals(this.columnName)) {
            return columnValue;
        }
        return null;
    }

    @Override
    public <T> Optional<T> value(String columnName, Class<T> clazz) {
        if (columnName.equals(this.columnName)) {
            return (Optional<T>) Optional.of(columnValue);
        }
        return Optional.empty();
    }
}
