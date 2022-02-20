package dev.yila.dormamu;

import dev.yila.dormamu.test.DatabaseTables;
import dev.yila.dormamu.test.DatabaseExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.opentest4j.AssertionFailedError;

import static dev.yila.dormamu.FakeTables.TABLE_ONE;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DatabaseTables(FakeTables.class)
@ExtendWith(DatabaseExtension.class)
public class TableChangesTest {

    private final static Row ROW_ONE = new FakeRow("1", "name", "Me");

    @Test
    void noChanges(Db db) {
        db.when("Do nothing", () -> {})
                .expect(Changes::isEmpty);
    }

    @Test
    void newRowInTable(Db db) {
        db.when("Add a new row in table " + TABLE_ONE, () ->
            getFakeTables(db).insertRow(TABLE_ONE, ROW_ONE)
        ).expect(changes -> changes.areExactly(1)
                .newRowIn(TABLE_ONE)
                .allValidated());
    }

    @Test
    void changeShouldBeValidated(Db db) {
        assertThrows(AssertionFailedError.class, () -> {
            db.when("Add a new row", () ->
                getFakeTables(db).insertRow(TABLE_ONE, ROW_ONE)
            ).expect(Changes::allValidated);
        });
    }

    @Test
    void deleteRowInTable(Db db) {
        getFakeTables(db).insertRow(TABLE_ONE, ROW_ONE);
        db.when("Delete row in table " + TABLE_ONE, () ->
                getFakeTables(db).deleteRow(TABLE_ONE, ROW_ONE)
        ).expect(changes -> changes.areExactly(1)
                .deletedRowIn(TABLE_ONE)
                .allValidated());
    }

    private FakeTables getFakeTables(Db db) {
        return (FakeTables) db.getTables();
    }
}
