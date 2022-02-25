package dev.yila.dormamu.test;

import dev.yila.dormamu.Changes;
import dev.yila.dormamu.report.ReportData;

public class ValidationChange {

    private final String testClassName;
    private final String testName;
    private final String description;
    private final Changes changes;
    private final ReportData before;
    private final ReportData after;

    public ValidationChange(String testClassName, String testName, String description,
                            Changes changes, ReportData before, ReportData after) {
        this.testClassName = testClassName;
        this.testName = testName;
        this.description = description;
        this.changes = changes;
        this.before = before;
        this.after = after;
    }

    public String getTestClassName() {
        return testClassName;
    }

    public String getTestName() {
        return testName;
    }

    public String getDescription() {
        return description;
    }

    public Changes getChanges() {
        return changes;
    }

    public ReportData getBefore() {
        return before;
    }

    public ReportData getAfter() {
        return after;
    }

    @Override
    public String toString() {
        return "Change: " + getDescription() + " in class: " + getTestClassName()
                + " in method: " + getTestName() + "\r\n" + changes.getResultAsString();
    }
}
