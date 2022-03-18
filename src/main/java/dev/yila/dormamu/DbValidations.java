package dev.yila.dormamu;

import java.util.function.Function;

import static dev.yila.dormamu.test.DatabaseExtension.showMessageInConsole;
import static org.junit.jupiter.api.Assertions.*;

public class DbValidations {

    private final Changes changes;

    public DbValidations(Changes changes) {
        this.changes = changes;
    }

    public void expect(Function<Changes, Boolean> function) {
        boolean result = function.apply(changes);
        if (!result) {
            showMessageInConsole(changes.getResultAsString());
            fail("Need to validate all changes done in database.");
        }
    }
}
