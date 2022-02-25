package dev.yila.dormamu.report;

import dev.yila.dormamu.Tables;
import dev.yila.dormamu.test.ValidationChange;

import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.stream.Collectors;

import static dev.yila.dormamu.test.DatabaseExtension.showMessageInConsole;

public class DefaultReportGenerator implements ReportGenerator {
    @Override
    public ReportData before(Tables tables) {
        return null;
    }

    @Override
    public ReportData after(Tables tables) {
        return null;
    }

    @Override
    public void generate(List<ValidationChange> list) {
        try {
            File tmpFile = File.createTempFile("report_", ".txt");
            FileWriter writer = new FileWriter(tmpFile);
            writer.write(list.stream().map(ValidationChange::toString).collect(Collectors.joining("\r\n")));
            writer.close();
            showMessageInConsole("Database validations report generated in: " + tmpFile.getAbsolutePath());
        } catch (Exception e) {
            showMessageInConsole("Error generating report.");
            e.printStackTrace();
        }
    }
}
