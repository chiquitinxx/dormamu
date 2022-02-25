package dev.yila.dormamu;

import dev.yila.dormamu.report.ReportData;
import dev.yila.dormamu.report.ReportGenerator;
import dev.yila.dormamu.test.ValidationChange;
import dev.yila.dormamu.test.ValidationChangesStore;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class Db implements ChangesCalculator {

    private final ValidationChangesStore validationChangesStore;
    private Tables tables;
    private ReportGenerator reportGenerator;

    public Db(ValidationChangesStore validationChangesStore) {
        this.validationChangesStore = validationChangesStore;
    }

    public Db withTables(Tables tables) {
        this.tables = tables;
        return this;
    }

    public Db withReportGenerator(ReportGenerator reportGenerator) {
        this.reportGenerator = reportGenerator;
        return this;
    }

    public DbValidations when(String description, Runnable runnable) {
        State dbState = tables.getState();
        ReportData before = reportGenerator.before(tables);
        assertDoesNotThrow(runnable::run, "Exception in when : " + description);
        Changes changes = between(dbState, tables.getState(), description);
        addChangeToStore(description, changes, before, reportGenerator.after(tables));
        return new DbValidations(changes);
    }

    private void addChangeToStore(String description, Changes changes, ReportData before, ReportData after) {
        validationChangesStore.putChange(new ValidationChange(
                null, null, description, changes, before, after));
    }

    public Tables getTables() {
        return this.tables;
    }
}
