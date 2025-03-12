package edu.ucsd.cse110.habitizer.app.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RoutineDao {
    @Query("SELECT * FROM routines ORDER BY id ASC")
    List<RoutineEntity> getAll();

    @Query("SELECT * FROM routines WHERE id = :id")
    RoutineEntity getById(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(RoutineEntity routine);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<RoutineEntity> routines);

    @Query("DELETE FROM routines WHERE id = :id")
    void delete(int id);

    @Query("UPDATE routines SET name = :newName WHERE id = :id")
    void updateRoutineName(int id, String newName);

    @Query("UPDATE routines SET goalTime = :newGoalTime WHERE id = :id")
    void updateGoalTime(int id, long newGoalTime);
} 