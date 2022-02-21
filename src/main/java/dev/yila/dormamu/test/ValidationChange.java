package dev.yila.dormamu.test;

import dev.yila.dormamu.Changes;

public class ValidationChange {

    private final String testClassName;
    private final String testName;
    private final String description;
    private final Changes changes;

    public ValidationChange(String testClassName, String testName, String description, Changes changes) {
        this.testClassName = testClassName;
        this.testName = testName;
        this.description = description;
        this.changes = changes;
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

    @Override
    public String toString() {
        return "Change: " + description + " in class: " + testClassName
                + " in method: " + testName + changes.getResultAsString();
    }
}
