package dev.yila.dormamu;

import dev.yila.dormamu.test.ValidationChange;
import dev.yila.dormamu.test.ValidationChangesStore;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class Db<T extends Tables> implements ChangesCalculator {

    private final Tables tables;
    private final ValidationChangesStore validationChangesStore;

    public Db(Tables tables, ValidationChangesStore validationChangesStore) {
        this.tables = tables;
        this.validationChangesStore = validationChangesStore;
    }

    public DbValidations when(String description, Runnable runnable) {
        State dbState = tables.getState();
        assertDoesNotThrow(runnable::run, "Exception in when : " + description);
        Changes changes = between(dbState, tables.getState(), description);
        addChangeToStore(description, changes);
        return new DbValidations(changes);
    }

    private void addChangeToStore(String description, Changes changes) {
        validationChangesStore.putChange(new ValidationChange(
                null, null, description, changes));
    }

    public Tables getTables() {
        return this.tables;
    }
}
