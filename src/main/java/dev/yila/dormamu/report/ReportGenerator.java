package dev.yila.dormamu.report;

import dev.yila.dormamu.Tables;
import dev.yila.dormamu.test.ValidationChange;

import java.util.List;

public interface ReportGenerator {
    ReportData before(Tables tables);
    ReportData after(Tables tables);
    void generate(List<ValidationChange> list);
}
