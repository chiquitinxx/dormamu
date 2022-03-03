package dev.yila.dormamu;

import dev.yila.dormamu.report.ReportData;
import dev.yila.dormamu.report.ReportDataProvider;

import static org.mockito.Mockito.mock;

public class FakeReportDataProvider implements ReportDataProvider {
    @Override
    public ReportData before(Tables tables) {
        return mock(ReportData.class);
    }

    @Override
    public ReportData after(Tables tables) {
        return mock(ReportData.class);
    }
}
