package dev.yila.dormamu;

import dev.yila.dormamu.test.DatabaseTables;
import dev.yila.dormamu.test.DatabaseExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.opentest4j.AssertionFailedError;

import static dev.yila.dormamu.FakeTables.TABLE_ONE;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

@DatabaseTables(FakeTables.class)
@ExtendWith(DatabaseExtension.class)
public class TableChangesTest {

    @Test
    void noChanges(Db db) {
        db.when("Do nothing", () -> {})
                .expect(Changes::isEmpty);
    }

    @Test
    void newRowInTable(Db db) {
        db.when("Add a new row in table " + TABLE_ONE, () ->
            getFakeTables(db).insertRow(TABLE_ONE, mock(Row.class))
        ).expect(changes -> changes.areExactly(1)
                .newRowIn(TABLE_ONE)
                .allValidated());
    }

    @Test
    void changeShouldBeValidated(Db db) {
        assertThrows(AssertionFailedError.class, () -> {
            db.when("Add a new row", () ->
                getFakeTables(db).insertRow(TABLE_ONE, mock(Row.class))
            ).expect(Changes::allValidated);
        });
    }

    @Test
    void deleteRowInTable(Db db) {
        Row row = mock(Row.class);
        getFakeTables(db).insertRow(TABLE_ONE, row);
        db.when("Delete row in table " + TABLE_ONE, () ->
                getFakeTables(db).deleteRow(TABLE_ONE, row)
        ).expect(changes -> changes.areExactly(1)
                .deletedRowIn(TABLE_ONE)
                .allValidated());
    }

    private FakeTables getFakeTables(Db db) {
        return (FakeTables) db.getTables();
    }
}
