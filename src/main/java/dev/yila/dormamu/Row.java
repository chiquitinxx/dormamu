package dev.yila.dormamu;

import java.util.Optional;

public interface Row {
    String getId();
    boolean equalColumns(Row row);
    String string(String columnName);
    <T> Optional<T> value(String columnName, Class<T> clazz);
}
