package dev.yila.dormamu;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class Changes {

    private final Set<Change> changes;
    private Set<Change> validatedChanges;

    public Changes(Set<Change> changes) {
        Objects.requireNonNull(changes);
        this.changes = changes;
        this.validatedChanges = new HashSet<>();
    }

    public boolean isEmpty() {
        return changes.size() == 0;
    }

    public Changes areExactly(int expectedTotalNumberOfChanges) {
        assertEquals(expectedTotalNumberOfChanges, this.changes.size(), "Total number of changes in database is not the expected.");
        return this;
    }

    public Changes newRowIn(String table) {
        Change insertionChange = findOneChangeInTable(table, change -> change.getType().equals(ChangeType.NEW_ROW));
        validatedChanges.add(insertionChange);
        return this;
    }

    public Changes deletedRowIn(String table) {
        Change deletionChange = findOneChangeInTable(table, change -> change.getType().equals(ChangeType.DELETED_ROW));
        validatedChanges.add(deletionChange);
        return this;
    }

    public boolean allValidated() {
        assertEquals(validatedChanges.size(), changes.size(), "Number of db changes: " + changes.size()
                + ", number of changes validated: " + validatedChanges.size());
        return validatedChanges.size() == changes.size();
    }

    private Change findOneChangeInTable(String table, Predicate<Change> predicate) {
        List<Change> matchChanges = this.changes.stream()
                .filter(change -> change.getTableName().equals(table)
                        && predicate.test(change))
                .collect(Collectors.toList());
        if (matchChanges.size() > 1) {
            fail("More than 1 change matches this validation, expected only 1 change to match.");
        }
        assertEquals(1, matchChanges.size());
        return matchChanges.get(0);
    }
}
