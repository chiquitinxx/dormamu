package dev.yila.dormamu;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public Changes newRowIn(String table, Predicate<Row> newRowValidations) {
        Change insertionChange = findOneChangeInTable(table, change -> change.getType().equals(ChangeType.NEW_ROW)
                && newRowValidations.test(change.getAfter()));
        validatedChanges.add(insertionChange);
        return this;
    }

    public Changes newRowIn(String table) {
        return newRowIn(table, row -> true);
    }

    public Changes deletedRowIn(String table, Predicate<Row> deletedRowValidations) {
        Change deletionChange = findOneChangeInTable(table, change -> change.getType().equals(ChangeType.DELETED_ROW)
                && deletedRowValidations.test(change.getBefore()));
        validatedChanges.add(deletionChange);
        return this;
    }

    public Changes deletedRowIn(String table) {
        return deletedRowIn(table, row -> true);
    }

    public Changes updatedRowIn(String table, BiPredicate<Row, Row> validation) {
        Change deletionChange = findOneChangeInTable(table, change -> change.getType().equals(ChangeType.UPDATED_ROW)
                && validation.test(change.getBefore(), change.getAfter()));
        validatedChanges.add(deletionChange);
        return this;
    }

    public Changes updatedRowIn(String table) {
        return updatedRowIn(table, (before, after) -> true);
    }

    public boolean allValidated() {
        boolean somethingNotValidate = validatedChanges.size() != changes.size();
        return !somethingNotValidate;
    }

    public Stream<ValidatedChange> validatedChanges() {
        return changes.stream()
                .map(change -> new ValidatedChange(change, validatedChanges.contains(change)));
    }

    public String getResultAsString() {
        String allChanges = "\r\n";
        if (changes.size() > 0) {
            allChanges += "-- " + (allValidated() ? " (SUCCESS)" : " (FAIL)") + "... validated (v) or not validated(x)\r\n";
            allChanges += validatedChanges().map(validatedChange -> {
                if (validatedChange.isValidated()) {
                    return "??? " + validatedChange.getChange().toString();
                } else {
                    return "??? " + validatedChange.getChange().toString();
                }
            }).collect(Collectors.joining("\r\n"));
            allChanges += "\r\n" + "-- End changes" + "\r\n";
        }
        return allChanges;
    }

    public boolean validationsPassed() {
        return isEmpty() || allValidated();
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
