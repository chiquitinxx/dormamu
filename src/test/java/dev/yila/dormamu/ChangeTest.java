package dev.yila.dormamu;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.mock;

public class ChangeTest {

    @Test
    public void equalChanges() {
        Row row = mock(Row.class);
        assertNotEquals(
                Change.insert("t", row),
                Change.insert("r", row));
        assertEquals(
                Change.delete("d", row),
                Change.delete("d", row)
        );
    }
}
