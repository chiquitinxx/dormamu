package dev.yila.dormamu;

import dev.yila.dormamu.report.ReportGenerator;
import dev.yila.dormamu.test.ValidationChange;

import java.util.List;

public class FakeReportGenerator implements ReportGenerator {

    @Override
    public void generate(List<ValidationChange> list) throws Exception {
    }
}
