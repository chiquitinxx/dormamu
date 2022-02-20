package dev.yila.dormamu;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface ChangesCalculator {

    default Changes between(DbState before, DbState after) {
        Set<Change> allChanges = before.getTablesRows().keySet().stream()
                .map(table -> getTableChanges(table, before.getTablesRows().get(table), after.getTablesRows().get(table)))
                .reduce(new HashSet<>(), (changes, tableChanges) ->
                        Stream.concat(changes.stream(), tableChanges.stream())
                                .collect(Collectors.toSet()));
        return new Changes(allChanges);
    }

    private Set<Change> getTableChanges(String table, List<Row> rowsBefore, List<Row> rowsAfter) {
        Set<Change> changes = new HashSet<>();
        changes.addAll(rowsDeleted(table, rowsBefore, rowsAfter));
        changes.addAll(rowsUpdated(table, rowsBefore, rowsAfter));
        changes.addAll(rowsInserted(table, rowsBefore, rowsAfter));
        return changes;
    }

    private Collection<? extends Change> rowsUpdated(String table, List<Row> rowsBefore, List<Row> rowsAfter) {
        //TODO
        return Collections.emptySet();
    }

    private Collection<? extends Change> rowsDeleted(String table, List<Row> rowsBefore, List<Row> rowsAfter) {
        //TODO
        Set<Change> deleted = new HashSet<>();
        rowsBefore.forEach(row -> {
            if (!rowsAfter.contains(row)) {
                deleted.add(Change.delete(table, row));
            }
        });
        return deleted;
    }

    private Collection<? extends Change> rowsInserted(String table, List<Row> rowsBefore, List<Row> rowsAfter) {
        //TODO
        Set<Change> inserted = new HashSet<>();
        rowsAfter.forEach(row -> {
            if (!rowsBefore.contains(row)) {
                inserted.add(Change.insert(table, row));
            }
        });
        return inserted;
    }
}
