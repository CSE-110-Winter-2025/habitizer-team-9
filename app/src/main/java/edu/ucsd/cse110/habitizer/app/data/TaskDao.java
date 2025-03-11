package edu.ucsd.cse110.habitizer.app.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TaskDao {
    @Query("SELECT * FROM tasks WHERE routineId = :routineId ORDER BY orderIndex ASC")
    List<TaskEntity> getTasksByRoutineId(int routineId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(TaskEntity task);

    @Query("DELETE FROM tasks WHERE id = :id")
    void deleteById(int id);

    @Query("DELETE FROM tasks WHERE routineId = :routineId")
    void deleteAllByRoutineId(int routineId);
    
    @Query("SELECT MAX(orderIndex) FROM tasks WHERE routineId = :routineId")
    Integer getMaxOrderIndex(int routineId);
    
    @Query("SELECT MAX(id) FROM tasks")
    Integer getMaxTaskId();

    @Query("UPDATE tasks SET name = :newName WHERE id = :taskId")
    void updateTaskName(int taskId, String newName);
    
    @Query("UPDATE tasks SET orderIndex = :orderIndex WHERE id = :taskId")
    void updateTaskOrderIndex(int taskId, int orderIndex);

} 