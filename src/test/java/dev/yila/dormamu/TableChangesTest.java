package dev.yila.dormamu;

import dev.yila.dormamu.test.DatabaseTables;
import dev.yila.dormamu.test.DatabaseExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@DatabaseTables(FakeTables.class)
@ExtendWith(DatabaseExtension.class)
public class TableChangesTest {

    @Test
    void noChanges(Db db) {
        db.when("Do nothing", () -> {})
                .expect(Changes::isEmpty);
    }
}
