package edu.ucsd.cse110.habitizer.lib.domain;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class Routine {
    private String name;
    private Map<Integer, Task> tasks;
    private boolean isStarted;

    private Duration goalTime;
    private Instant routineStartTime;
    private Instant taskStartTime;

    public Routine(String name) {
        this.name = name;
        tasks = new HashMap<>();
        isStarted = false;
        goalTime = Duration.ofSeconds(0);
    }

    public void startRoutine(Instant now) {
        isStarted = true;
        routineStartTime = now;
        taskStartTime = now;
    }

    public void checkOffTask(Instant now, int id) {
        taskStartTime = now;
        // Once task class is set up, make sure to complete this method
    }

    public void setGoalTime(int timeInMinutes) {
        int seconds = timeInMinutes * 60;
        goalTime = Duration.ofSeconds(seconds);
    }

    public String getName() {
        return name;
    }

}
