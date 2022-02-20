package dev.yila.dormamu;

import dev.yila.dormamu.test.Tables;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class Db implements ChangesCalculator {

    private final Tables tables;

    public Db(Tables tables) {
        this.tables = tables;
    }

    public DbValidations when(String description, Runnable runnable) {
        DbState dbState = tables.getState();
        assertDoesNotThrow(runnable::run, "Exception in when : " + description);
        Changes changes = between(dbState, tables.getState());
        return new DbValidations(changes);
    }

    public Tables getTables() {
        return this.tables;
    }
}
