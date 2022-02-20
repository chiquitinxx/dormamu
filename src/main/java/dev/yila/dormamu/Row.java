package dev.yila.dormamu;

import java.util.Optional;

public interface Row {
    String getId();
    boolean equalColumns(Row row);
    <T> Optional<T> getColumnValue(String columnName, Class<T> clazz);
}
