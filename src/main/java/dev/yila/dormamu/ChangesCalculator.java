package dev.yila.dormamu;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface ChangesCalculator {

    default Changes between(State before, State after) {
        Set<Change> allChanges = before.getState().keySet().stream()
                .map(table -> getTableChanges(table, before.getState().get(table), after.getState().get(table)))
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
        Set<Change> updated = new HashSet<>();
        rowsBefore.forEach(row -> {
            rowsAfter.stream()
                    .filter(rowAfter -> rowAfter.getId().equals(row.getId()))
                    .findFirst()
                    .flatMap(rowAfter -> rowAfter.equalValues(row) ? Optional.empty() : Optional.of(rowAfter))
                    .ifPresent(rowAfter -> updated.add(Change.update(table, row, rowAfter)));
        });
        return updated;
    }

    private Collection<? extends Change> rowsDeleted(String table, List<Row> rowsBefore, List<Row> rowsAfter) {
        Set<Change> deleted = new HashSet<>();
        rowsBefore.forEach(row -> {
            if (rowsMissingId(rowsAfter, row.getId())) {
                deleted.add(Change.delete(table, row));
            }
        });
        return deleted;
    }

    private Collection<? extends Change> rowsInserted(String table, List<Row> rowsBefore, List<Row> rowsAfter) {
        Set<Change> inserted = new HashSet<>();
        rowsAfter.forEach(row -> {
            if (rowsMissingId(rowsBefore, row.getId())) {
                inserted.add(Change.insert(table, row));
            }
        });
        return inserted;
    }

    private boolean rowsMissingId(List<Row> rows, String id) {
        return rows.stream().noneMatch(row -> row.getId().equals(id));
    }
}
