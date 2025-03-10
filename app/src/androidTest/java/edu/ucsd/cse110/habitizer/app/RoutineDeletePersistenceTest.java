package edu.ucsd.cse110.habitizer.app;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import edu.ucsd.cse110.habitizer.app.data.AppDatabase;
import edu.ucsd.cse110.habitizer.app.data.RoomRoutineRepository;
import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.lib.domain.RoutineRepository;
import edu.ucsd.cse110.habitizer.lib.domain.Task;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class RoutineDeletePersistenceTest {
    private AppDatabase db;
    private RoutineRepository repository;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        repository = new RoomRoutineRepository(db);
    }

    @After
    public void closeDb() {
        db.close();
    }

    @Test
    public void testDeleteRoutinePersistence() throws InterruptedException {
        Routine routine1 = new Routine(1, "Morning Routine");
        Routine routine2 = new Routine(2, "Evening Routine");
        Routine routine3 = new Routine(3, "Weekend Routine");
        
        repository.save(routine1);
        repository.save(routine2);
        repository.save(routine3);
        
        Thread.sleep(1000);
        
        repository.deleteRoutine(2);
        
        Thread.sleep(1000);
        
        RoutineRepository newRepository = new RoomRoutineRepository(db);
        
        CountDownLatch latch = new CountDownLatch(1);
        
        newRepository.findAll().observe(routines -> {
            if (routines != null) {
                latch.countDown();
            }
        });
        
        assertTrue("Timed out waiting for data to load", latch.await(5, TimeUnit.SECONDS));
        
        List<Routine> routines = newRepository.findAll().getValue();
        assertNotNull("Routines should not be null", routines);
        assertEquals("Should have 2 routines", 2, routines.size());
        
        boolean hasRoutine2 = false;
        for (Routine routine : routines) {
            if (routine.id() == 2) {
                hasRoutine2 = true;
                break;
            }
        }
        
        assertFalse("Routine 2 should not exist after deletion", hasRoutine2);
    }
    
    @Test
    public void testDeleteTaskPersistence() throws InterruptedException {
        Routine routine = new Routine(1, "Test Routine");
        repository.save(routine);
        
        Task task1 = new Task(1, "Task 1");
        Task task2 = new Task(2, "Task 2");
        Task task3 = new Task(3, "Task 3");
        
        repository.addTask(routine, task1);
        repository.addTask(routine, task2);
        repository.addTask(routine, task3);
        
        Thread.sleep(1000);
        
        repository.deleteTask(2);
        
        Thread.sleep(1000);
        
        RoutineRepository newRepository = new RoomRoutineRepository(db);
        
        CountDownLatch latch = new CountDownLatch(1);
        
        newRepository.findAllMappings().observe(map -> {
            if (map != null && !map.isEmpty()) {
                latch.countDown();
            }
        });
        
        assertTrue("Timed out waiting for data to load", latch.await(5, TimeUnit.SECONDS));
        
        Map<Routine, List<Task>> routineTaskMap = newRepository.findAllMappings().getValue();
        assertNotNull("Routine task map should not be null", routineTaskMap);
        
        Routine savedRoutine = null;
        for (Routine r : routineTaskMap.keySet()) {
            if (r.id() == 1) {
                savedRoutine = r;
                break;
            }
        }
        
        assertNotNull("Routine should be found", savedRoutine);
        
        List<Task> tasks = routineTaskMap.get(savedRoutine);
        assertNotNull("Tasks should not be null", tasks);
        assertEquals("Should have 2 tasks", 2, tasks.size());
        
        boolean hasTask2 = false;
        for (Task task : tasks) {
            if (task.getId() == 2) {
                hasTask2 = true;
                break;
            }
        }
        
        assertFalse("Task 2 should not exist after deletion", hasTask2);
    }
    
    @Test
    public void testDeleteRoutineWithTasksPersistence() throws InterruptedException {
        Routine routine = new Routine(1, "Test Routine");
        repository.save(routine);
        
        Task task1 = new Task(1, "Task 1");
        Task task2 = new Task(2, "Task 2");
        Task task3 = new Task(3, "Task 3");
        
        repository.addTask(routine, task1);
        repository.addTask(routine, task2);
        repository.addTask(routine, task3);
        
        Thread.sleep(1000);
        
        repository.deleteRoutine(1);
        
        Thread.sleep(1000);
        
        RoutineRepository newRepository = new RoomRoutineRepository(db);
        
        CountDownLatch latch = new CountDownLatch(1);
        
        newRepository.findAll().observe(routines -> {
            latch.countDown();
        });
        
        assertTrue("Timed out waiting for data to load", latch.await(5, TimeUnit.SECONDS));
        
        List<Routine> routines = newRepository.findAll().getValue();
        assertTrue("Routines list should be empty", routines == null || routines.isEmpty());
        
        Map<Routine, List<Task>> routineTaskMap = newRepository.findAllMappings().getValue();
        assertTrue("Routine task map should be empty", routineTaskMap == null || routineTaskMap.isEmpty());
    }
} 