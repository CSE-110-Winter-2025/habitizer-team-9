package edu.ucsd.cse110.habitizer.lib.domain;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import edu.ucsd.cse110.observables.Subject;

public class Routine {

    private @Nullable  String name;

    private int id;

    private boolean isStarted;

    private long goalTime;
    private Instant routineStartTime;
    private Instant taskStartTime;

    public RoutineTimer routineTimer;


    public Routine(int id, String name) {
        this.id = id;
        this.name = name;
        isStarted = false;
        goalTime = 0;
    }

    public void startRoutine(Instant now) {
        isStarted = true;
        routineStartTime = now;
        taskStartTime = now;
    }


    public String getName() {
        return name;
    }

    public void rename(@NonNull String newName)
    {
        this.name = newName;
    }

    public boolean getIsStarted() {
        return isStarted;
    }

    public void setIsStarted(boolean isStarted) {
        this.isStarted = isStarted;
    }

    public Instant getRoutineStartTime() {
        return routineStartTime;
    }

    public void setRoutineStartTime(Instant instant) {
        this.routineStartTime = instant;
    }

    public Instant getTaskStartTime() {
        return taskStartTime;
    }

    public void setTaskStartTime(Instant instant) {
        this.taskStartTime = instant;
    }

    public long getGoalTime() {
        return goalTime;
    }

    public void setGoalTime(long goalTime) {
        this.goalTime = goalTime;
    }

    public @Nullable Integer id() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Routine routine = (Routine) o;
        return id() == routine.id();
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id());
    }


}
