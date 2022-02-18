package dev.yila.dormamu;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

public class DbValidations {

    private final Changes changes;

    public DbValidations(Changes changes) {
        this.changes = changes;
    }

    void expect(Function<Changes, Boolean> function) {
        assertTrue(function.apply(changes));
    }
}
