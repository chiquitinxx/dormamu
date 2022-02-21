package dev.yila.dormamu.test;

import java.util.ArrayList;
import java.util.List;

public class ValidationChangesStore {

    private List<ValidationChange> list;

    public ValidationChangesStore() {
        this.list = new ArrayList<>();
    }

    public void putChange(ValidationChange validationChange) {
        this.list.add(validationChange);
    }

    public List<ValidationChange> getChanges() {
        return this.list;
    }

    public void setChanges(List<ValidationChange> changes) {
        this.list = changes;
    }
}
