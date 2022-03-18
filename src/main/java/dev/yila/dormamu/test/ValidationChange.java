package dev.yila.dormamu.test;

import dev.yila.dormamu.Changes;
import dev.yila.dormamu.report.ReportData;

public class ValidationChange {

    private final String testClassName;
    private final String testMethodName;
    private final Changes changes;
    private final ReportData before;
    private final ReportData after;
    private final boolean testSuccess;

    public ValidationChange(String testClassName, String testMethodName,
                            Changes changes, ReportData before, ReportData after, boolean testSuccess) {
        this.testClassName = testClassName;
        this.testMethodName = testMethodName;
        this.changes = changes;
        this.before = before;
        this.after = after;
        this.testSuccess = testSuccess;
    }

    public String getTestClassName() {
        return testClassName;
    }

    public String getTestMethodName() {
        return testMethodName;
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

    public boolean isTestSuccess() {
        return testSuccess;
    }

    @Override
    public String toString() {
        return "Change in class: " + getTestClassName()
                + " in method: " + getTestMethodName() + " with result: " + (isTestSuccess() ? "SUCCESS" : "FAIL")
                + "\r\n" + changes.getResultAsString();
    }
}
