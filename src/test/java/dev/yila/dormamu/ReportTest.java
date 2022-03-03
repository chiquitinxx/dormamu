package dev.yila.dormamu;

import dev.yila.dormamu.report.DbReport;
import dev.yila.dormamu.report.ReportDataProvider;
import dev.yila.dormamu.test.DatabaseExtension;
import dev.yila.dormamu.test.DbTables;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@DbTables(FakeTables.class)
@DbReport(generator = FakeReportGenerator.class, dataProvider = FakeReportDataProvider.class)
@ExtendWith(DatabaseExtension.class)
public class ReportTest {

    @Test
    void generateReportData(Db db) {
        ReportDataProvider reportDataProvider = mock(ReportDataProvider.class);
        db.withReportDataProvider(reportDataProvider);

        db.when("Do nothing", () -> {}).expect(Changes::isEmpty);

        verify(reportDataProvider).before(any());
        verify(reportDataProvider).after(any());
    }
}
