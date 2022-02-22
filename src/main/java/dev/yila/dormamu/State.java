package dev.yila.dormamu;

import java.util.List;
import java.util.Map;

public class State {

    private final Map<String, List<Row>> tablesRows;

    public State(Map<String, List<Row>> tablesRows) {
        this.tablesRows = tablesRows;
    }

    public Map<String, List<Row>> getState() {
        return this.tablesRows;
    }
}
