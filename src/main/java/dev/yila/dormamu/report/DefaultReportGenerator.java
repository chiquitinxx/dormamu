package dev.yila.dormamu.report;

import dev.yila.dormamu.test.DatabaseTestChange;

import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.stream.Collectors;

import static dev.yila.dormamu.test.DatabaseExtension.showMessageInConsole;

public class DefaultReportGenerator implements ReportGenerator {
    @Override
    public void generate(List<DatabaseTestChange> list) throws Exception {
        File tmpFile = File.createTempFile("report_", ".txt");
        FileWriter writer = new FileWriter(tmpFile);
        writer.write(list.stream().map(DatabaseTestChange::toString).collect(Collectors.joining("\r\n")));
        writer.close();
        showMessageInConsole("Database validations report generated in: " + tmpFile.getAbsolutePath());
    }
}
