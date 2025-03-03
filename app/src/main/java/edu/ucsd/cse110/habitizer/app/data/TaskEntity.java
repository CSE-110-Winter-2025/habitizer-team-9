package edu.ucsd.cse110.habitizer.app.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import edu.ucsd.cse110.habitizer.lib.domain.Task;

@Entity(
    tableName = "tasks",
    foreignKeys = @ForeignKey(
        entity = RoutineEntity.class,
        parentColumns = "id",
        childColumns = "routineId",
        onDelete = ForeignKey.CASCADE
    ),
    indices = {@Index("routineId")}
)
public class TaskEntity {
    @PrimaryKey
    public final int id;

    @NonNull
    public String name;

    public final int routineId;
    
    public final boolean isCheckedOff;
    
    public final int orderIndex;

    public TaskEntity(int id, @NonNull String name, int routineId, boolean isCheckedOff, int orderIndex) {
        this.id = id;
        this.name = name;
        this.routineId = routineId;
        this.isCheckedOff = isCheckedOff;
        this.orderIndex = orderIndex;
    }

    public static TaskEntity fromDomain(Task task, int routineId, int orderIndex) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }
        
        if (task.getId() == null) {
            throw new IllegalArgumentException("Task ID cannot be null");
        }
        
        return new TaskEntity(
            task.getId(),
            task.getName() != null ? task.getName() : "",
            routineId,
            task.getIsCheckedOff(),
            orderIndex
        );
    }

    public Task toDomain() {
        Task task = new Task(id, name);
        if (isCheckedOff) {
            task.checkOff();
        }
        return task;
    }

    public void setName(String name) {
        this.name = name;
    }

} 