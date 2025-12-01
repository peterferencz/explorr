package me.peterferencz.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import me.peterferencz.app.EventDispacher.Events;

public class EventDispacherTest {
    
    @Test
    void callbackRunsWhenDispatched() {
        var called = new boolean[]{false};

        EventDispacher.subscribe(Events.CLASSSELECTED,
            () -> called[0] = true);

        EventDispacher.dispatch(Events.CLASSSELECTED);

        assertTrue(called[0]);
    }

    @Test
    void multipleCallbacksRun() {
        var counter = new int[]{0};

        EventDispacher.subscribe(EventDispacher.Events.SAVEUMLDIAGRAM,
            () -> counter[0]++);
        EventDispacher.subscribe(EventDispacher.Events.SAVEUMLDIAGRAM,
            () -> counter[0]++);

        EventDispacher.dispatch(EventDispacher.Events.SAVEUMLDIAGRAM);

        assertEquals(2, counter[0]);
    }

    @Test
    void onlySubscribedEventTriggersCallbacks() {
        var called = new boolean[]{false};

        EventDispacher.subscribe(EventDispacher.Events.MANIFESTFILECHOOSEN,
            () -> called[0] = true);

        EventDispacher.dispatch(EventDispacher.Events.CLASSSELECTED);

        assertFalse(called[0]);
    }
}
