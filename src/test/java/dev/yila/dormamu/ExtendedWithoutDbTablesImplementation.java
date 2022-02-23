package dev.yila.dormamu;

import dev.yila.dormamu.test.DatabaseExtension;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(DatabaseExtension.class)
public class ExtendedWithoutDbTablesImplementation {

    @Test
    void failingParameterResolution(Db db) {
        assertNull(db.getTables());
    }
}
