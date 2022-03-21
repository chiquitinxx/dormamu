package dev.yila.dormamu.report;

import dev.yila.dormamu.test.DatabaseTestChange;

import java.util.List;

public interface ReportGenerator {
    void generate(List<DatabaseTestChange> list) throws Exception;
}
