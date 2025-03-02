package edu.ucsd.cse110.habitizer.app.data.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.Instant;

@Entity(tableName = "routines")
public class RoutineEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "is_started")
    private boolean isStarted;

    @ColumnInfo(name = "goal_time")
    private long goalTime;

    @ColumnInfo(name = "routine_start_time")
    private Instant routineStartTime;

    @ColumnInfo(name = "task_start_time")
    private Instant taskStartTime;

    public RoutineEntity(String name) {
        this.name = name;
        this.isStarted = false;
        this.goalTime = 0;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public boolean getIsStarted() { return isStarted; }
    public long getGoalTime() { return goalTime; }

    public Instant getRoutineStartTime() { return routineStartTime; }
    public Instant getTaskStartTime() { return taskStartTime; }

    public void setIsStarted(boolean isStarted) { this.isStarted = isStarted; }
    public void setGoalTime(long goalTime) { this.goalTime = goalTime; }
    public void setRoutineStartTime(Instant instant) { this.routineStartTime = instant; }
    public void setTaskStartTime(Instant instant) { this.taskStartTime = instant; }
}
