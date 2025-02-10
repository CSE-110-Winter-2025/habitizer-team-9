package edu.ucsd.cse110.habitizer.lib.domain;

import junit.framework.TestCase;

import java.time.Duration;
import java.time.Instant;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class RoutineTest extends TestCase {
    public void testStartRoutine() {
        var routine = new Routine(1, "Morning Routine");
        Instant routineStartTime = Instant.parse("2020-11-03T10:17:35.00Z");
        routine.startRoutine(routineStartTime);
        assertThat(routine.getIsStarted(), is(true));
        assertThat(routine.getRoutineStartTime(), is(routineStartTime));
        assertThat(routine.getTaskStartTime(), is(routineStartTime));
    }

    public void testSetGoalTime() {
        var routine = new Routine(1, "Morning Routine");
        routine.setGoalTime(2);
        Duration goalTime = Duration.ofSeconds(120);
        assertThat(routine.getGoalTime(), is(goalTime));
    }

    public void testId() {
        var routine = new Routine(1, "Morning Routine");
        assertThat(routine.id(), is(1));
    }
}