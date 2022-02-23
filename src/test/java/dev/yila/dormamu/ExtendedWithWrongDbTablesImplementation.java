package dev.yila.dormamu;

import dev.yila.dormamu.test.DatabaseExtension;
import dev.yila.dormamu.test.DbTables;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(DatabaseExtension.class)
@DbTables(FailingTables.class)
public class ExtendedWithWrongDbTablesImplementation {

    @Test
    void failingCreatingTables(Db db) {
        assertNull(db.getTables());
    }
}
