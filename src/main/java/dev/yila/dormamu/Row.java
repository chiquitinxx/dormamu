package dev.yila.dormamu;

import java.util.Map;
import java.util.Optional;

public interface Row {
    String getId();
    Map<String, ?> getColumns();
    boolean equalValues(Row row);
    String string(String columnName);
    <T> Optional<T> value(String columnName, Class<T> clazz);
}
