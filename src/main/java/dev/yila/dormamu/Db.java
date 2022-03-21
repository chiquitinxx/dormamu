package dev.yila.dormamu;

import dev.yila.dormamu.report.ReportData;
import dev.yila.dormamu.report.ReportDataProvider;
import dev.yila.dormamu.test.DatabaseTestChange;
import dev.yila.dormamu.test.ValidationChangesStore;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class Db implements ChangesCalculator {

    private final ValidationChangesStore validationChangesStore;
    private Tables tables;
    private ReportDataProvider reportDataProvider;

    public Db(ValidationChangesStore validationChangesStore) {
        this.validationChangesStore = validationChangesStore;
    }

    public Db withTables(Tables tables) {
        this.tables = tables;
        return this;
    }

    public Db withReportDataProvider(ReportDataProvider reportGenerator) {
        this.reportDataProvider = reportGenerator;
        return this;
    }

    public DbValidations when(Runnable runnable) {
        State dbState = tables.getState();
        ReportData before = reportDataProvider != null ? reportDataProvider.before(tables) : null;
        assertDoesNotThrow(runnable::run, "Not exception in when.");
        Changes changes = between(dbState, tables.getState());
        ReportData after = reportDataProvider != null ? reportDataProvider.after(tables) : null;
        addChangeToStore(changes, before, after);
        return new DbValidations(changes);
    }

    private void addChangeToStore(Changes changes, ReportData before, ReportData after) {
        validationChangesStore.putChange(new DatabaseTestChange(
                null, null, changes, before, after, false));
    }

    public Tables getTables() {
        return this.tables;
    }
}
