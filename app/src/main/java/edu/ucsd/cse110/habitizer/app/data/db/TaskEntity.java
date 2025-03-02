package edu.ucsd.cse110.habitizer.app.data.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tasks")
public class TaskEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "is_checked_off")
    private boolean isCheckedOff;

    @ColumnInfo(name = "routine_id") // Foreign key reference
    private int routineId;

    public TaskEntity(String name, int routineId) {
        this.name = name;
        this.routineId = routineId;
        this.isCheckedOff = false;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public int getRoutineId() { return routineId; }
    public boolean getIsCheckedOff() { return isCheckedOff; }

    public void checkOff() { this.isCheckedOff = !isCheckedOff; }
}
