package dev.yila.dormamu.test;

import java.util.ArrayList;
import java.util.List;

public class ValidationChangesStore {

    private List<DatabaseTestChange> list;

    public ValidationChangesStore() {
        this.list = new ArrayList<>();
    }

    public void putChange(DatabaseTestChange validationChange) {
        this.list.add(validationChange);
    }

    public List<DatabaseTestChange> getChanges() {
        return this.list;
    }

    public void setChanges(List<DatabaseTestChange> changes) {
        this.list = changes;
    }
}
