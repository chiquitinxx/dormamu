package dev.yila.dormamu;

public class FailingTables implements Tables {

    public FailingTables() {
        throw new RuntimeException("Error!");
    }

    @Override
    public State getState() {
        return null;
    }
}
