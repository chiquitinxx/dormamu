package dev.yila.dormamu;

import dev.yila.dormamu.test.DatabaseExtension;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

public class DbValidations {

    private final Changes changes;

    public DbValidations(Changes changes) {
        this.changes = changes;
    }

    void expect(Function<Changes, Boolean> function) {
        boolean result = function.apply(changes);
        if (!result) {
            DatabaseExtension.showMessageInConsole(changes.getResultAsString());
            fail("Need to validate all changes done in database.");
        }
    }
}
