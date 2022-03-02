package dev.yila.dormamu;

import dev.yila.dormamu.test.DbTables;
import dev.yila.dormamu.test.DatabaseExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.opentest4j.AssertionFailedError;

import static dev.yila.dormamu.FakeTables.TABLE_ONE;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DbTables(FakeTables.class)
@ExtendWith(DatabaseExtension.class)
public class TableChangesTest {

    private final static String ID_ONE = "1";
    private final static String COLUMN_ONE = "name";
    private final static Row ROW_ONE = new FakeRow(ID_ONE, COLUMN_ONE, "Me");
    private final static Row ROW_TWO = new FakeRow("2", COLUMN_ONE, "Two");

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
    void validateRowData(Db db) {
        db.when("Add a new row in table " + TABLE_ONE, () ->
                getFakeTables(db).insertRow(TABLE_ONE, ROW_ONE)
        ).expect(changes -> changes.areExactly(1)
                .newRowIn(TABLE_ONE, row -> row.value(COLUMN_ONE, String.class)
                        .orElse("Column not found").equals("Me"))
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

    @Test
    void validationsOnlyCanMatchOneChange(Db db) {
        getFakeTables(db).insertRow(TABLE_ONE, ROW_ONE).insertRow(TABLE_ONE, ROW_TWO);
        assertThrows(AssertionFailedError.class, () ->
                db.when("Delete row not identified", () ->
                        getFakeTables(db).deleteRow(TABLE_ONE, ROW_ONE)
                                .deleteRow(TABLE_ONE, ROW_TWO)
                ).expect(changes -> changes
                        .deletedRowIn(TABLE_ONE)
                        .allValidated())
        );
    }

    @Test
    void addAndDeleteRow(Db db) {
        getFakeTables(db).insertRow(TABLE_ONE, ROW_ONE);
        db.when("Add and delete row in table " + TABLE_ONE, () ->
                getFakeTables(db).deleteRow(TABLE_ONE, ROW_ONE)
                        .insertRow(TABLE_ONE, ROW_TWO)
        ).expect(changes -> changes.areExactly(2)
                .deletedRowIn(TABLE_ONE)
                .newRowIn(TABLE_ONE)
                .allValidated());
    }

    @Test
    void updateRow(Db db) {
        getFakeTables(db).insertRow(TABLE_ONE, ROW_ONE);
        db.when("Add and delete row in table " + TABLE_ONE, () ->
                getFakeTables(db).updateRow(TABLE_ONE, ID_ONE, COLUMN_ONE, "new value")
        ).expect(changes -> changes.areExactly(1)
                .updatedRowIn(TABLE_ONE)
                .allValidated());
    }

    @Test
    void validateUpdateRowColumns(Db db) {
        getFakeTables(db).insertRow(TABLE_ONE, ROW_ONE);
        db.when("Add and delete row in table " + TABLE_ONE, () ->
                getFakeTables(db).updateRow(TABLE_ONE, ID_ONE, COLUMN_ONE, "new value")
        ).expect(changes -> changes.areExactly(1)
                .updatedRowIn(TABLE_ONE, (before, after) ->
                        before.string(COLUMN_ONE).equals("Me")
                        && after.string(COLUMN_ONE).equals("new value")
                        && !before.equals(after))
                .allValidated() && changes.validationsPassed());
    }

    private FakeTables getFakeTables(Db db) {
        return (FakeTables) db.getTables();
    }
}
