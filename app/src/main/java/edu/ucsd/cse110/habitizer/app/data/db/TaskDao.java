package edu.ucsd.cse110.habitizer.app.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(TaskEntity task);

    @Update
    void update(TaskEntity task);

    @Query("DELETE FROM tasks WHERE id = :id")
    void delete(int id);

    @Query("SELECT * FROM tasks WHERE routine_id = :routineId ORDER BY id ASC")
    LiveData<List<TaskEntity>> getTasksForRoutine(int routineId);
}
