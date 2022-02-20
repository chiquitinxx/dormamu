package dev.yila.dormamu;

import java.util.List;
import java.util.Map;

public class DbState {

    private final Map<String, List<Row>> tablesRows;

    public DbState(Map<String, List<Row>> tablesRows) {
        this.tablesRows = tablesRows;
    }

    Map<String, List<Row>> getTablesRows() {
        return this.tablesRows;
    }
}
