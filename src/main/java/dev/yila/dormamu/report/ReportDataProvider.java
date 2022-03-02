package dev.yila.dormamu.report;

import dev.yila.dormamu.Tables;

public interface ReportDataProvider {
    ReportData before(Tables tables);
    ReportData after(Tables tables);
}
