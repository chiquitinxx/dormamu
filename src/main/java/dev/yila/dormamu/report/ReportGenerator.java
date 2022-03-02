package dev.yila.dormamu.report;

import dev.yila.dormamu.test.ValidationChange;

import java.util.List;

public interface ReportGenerator {
    void generate(List<ValidationChange> list) throws Exception;
}
