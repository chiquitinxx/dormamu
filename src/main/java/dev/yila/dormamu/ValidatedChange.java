package dev.yila.dormamu;

public class ValidatedChange {
    private final Change change;
    private final boolean validated;

    public ValidatedChange(Change change, boolean validated) {
        this.change = change;
        this.validated = validated;
    }

    public Change getChange() {
        return change;
    }

    public boolean isValidated() {
        return validated;
    }
}
