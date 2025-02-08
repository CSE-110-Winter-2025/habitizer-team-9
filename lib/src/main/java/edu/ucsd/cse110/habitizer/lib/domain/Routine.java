package edu.ucsd.cse110.habitizer.lib.domain;

import androidx.annotation.Nullable;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import edu.ucsd.cse110.observables.Subject;

public class Routine {
    private @Nullable  String name;

    private int id;

    private boolean isStarted;

    private Duration goalTime;
    private Instant routineStartTime;
    private Instant taskStartTime;

    public Routine(int id, String name) {
        this.id = id;
        this.name = name;
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

    public @Nullable Integer id() {
        return id;
    }



}
