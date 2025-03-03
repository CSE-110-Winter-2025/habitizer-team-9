package edu.ucsd.cse110.habitizer.app.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public interface TaskDao {
    @Query("SELECT * FROM tasks WHERE routineId = :routineId ORDER BY orderIndex ASC")
    List<TaskEntity> getTasksByRoutineId(int routineId);

    @Query("SELECT * FROM tasks WHERE id = :id")
    TaskEntity getById(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(TaskEntity task);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<TaskEntity> tasks);

    @Query("DELETE FROM tasks WHERE id = :id")
    void delete(int id);

    @Query("DELETE FROM tasks WHERE routineId = :routineId")
    void deleteAllByRoutineId(int routineId);
    
    @Query("SELECT MAX(orderIndex) FROM tasks WHERE routineId = :routineId")
    Integer getMaxOrderIndex(int routineId);
    
    @Query("SELECT MAX(id) FROM tasks")
    Integer getMaxTaskId();
} 