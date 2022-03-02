package dev.yila.dormamu;

import dev.yila.dormamu.report.ReportData;
import dev.yila.dormamu.report.ReportDataProvider;
import dev.yila.dormamu.test.ValidationChange;
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

    public DbValidations when(String description, Runnable runnable) {
        State dbState = tables.getState();
        ReportData before = reportDataProvider != null ? reportDataProvider.before(tables) : null;
        assertDoesNotThrow(runnable::run, "Exception in when : " + description);
        Changes changes = between(dbState, tables.getState(), description);
        ReportData after = reportDataProvider != null ? reportDataProvider.after(tables) : null;
        addChangeToStore(description, changes, before, after);
        return new DbValidations(changes);
    }

    private void addChangeToStore(String description, Changes changes, ReportData before, ReportData after) {
        validationChangesStore.putChange(new ValidationChange(
                null, null, description, changes, before, after, false));
    }

    public Tables getTables() {
        return this.tables;
    }
}
