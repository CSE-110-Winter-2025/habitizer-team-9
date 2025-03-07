package edu.ucsd.cse110.habitizer.app.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import edu.ucsd.cse110.habitizer.lib.domain.Routine;

@Entity(tableName = "routines")
public class RoutineEntity {
    @PrimaryKey
    public final int id;

    @NonNull
    public final String name;

    public final long goalTime;

    public RoutineEntity(int id, @NonNull String name, long goalTime) {
        this.id = id;
        this.name = name;
        this.goalTime = goalTime;
    }

    public static RoutineEntity fromDomain(Routine routine) {
        if (routine == null) {
            throw new IllegalArgumentException("Routine cannot be null");
        }
        
        if (routine.id() == null) {
            throw new IllegalArgumentException("Routine ID cannot be null");
        }
        
        return new RoutineEntity(
            routine.id(), 
            routine.getName() != null ? routine.getName() : "", 
            routine.getGoalTime()
        );
    }

    public Routine toDomain() {
        Routine routine = new Routine(id, name);
        routine.setGoalTime(goalTime);
        return routine;
    }
} 