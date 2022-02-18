package dev.yila.dormamu;

import dev.yila.dormamu.test.Tables;

public class Db {

    private final Tables tables;

    public Db(Tables tables) {
        this.tables = tables;
    }

    public DbValidations when(String description, Runnable runnable) {
        //TODO
        return new DbValidations(new Changes());
    }
}
