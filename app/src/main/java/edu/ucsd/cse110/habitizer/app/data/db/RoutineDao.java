package edu.ucsd.cse110.habitizer.app.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface RoutineDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(RoutineEntity routine);

    @Update
    void update(RoutineEntity routine);

    @Query("DELETE FROM routines WHERE id = :id")
    void delete(int id);

    @Query("SELECT * FROM routines ORDER BY id ASC")
    LiveData<List<RoutineEntity>> getAllRoutines();

    @Query("SELECT * FROM routines WHERE id = :id")
    LiveData<RoutineEntity> getRoutineById(int id);
}

