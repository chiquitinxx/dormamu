package dev.yila.dormamu.test;

import dev.yila.dormamu.DbState;

public interface Tables {
    /**
     * Get immutable tables state
     * @return
     */
    DbState getState();
}
