package dev.yila.dormamu.test;

import dev.yila.dormamu.DbState;

public interface Tables {
    /**
     * Get tables immutable state
     * @return
     */
    DbState getState();
}
