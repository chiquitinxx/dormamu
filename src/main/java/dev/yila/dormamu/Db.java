package dev.yila.dormamu;

import dev.yila.dormamu.test.ValidationChange;
import dev.yila.dormamu.test.ValidationChangesStore;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class Db implements ChangesCalculator {

    private final ValidationChangesStore validationChangesStore;
    private Tables tables;

    public Db(ValidationChangesStore validationChangesStore) {
        this.validationChangesStore = validationChangesStore;
    }

    public Db with(Tables tables) {
        this.tables = tables;
        return this;
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
