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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import edu.ucsd.cse110.habitizer.app.data.AppDatabase;
import edu.ucsd.cse110.habitizer.app.data.RoomRoutineRepository;
import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.lib.domain.RoutineRepository;
import edu.ucsd.cse110.habitizer.lib.domain.Task;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class RoutinePersistenceTest {
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
    public void testRoutinePersistence() throws InterruptedException {
        // Create a routine
        Routine routine = new Routine(1, "Friday Evening");
        routine.setGoalTime(90); // 90 minutes
        
        // Save the routine
        repository.save(routine);
        
        // Add tasks to the routine
        repository.addTask(routine, new Task(1, "Dinner"));
        repository.addTask(routine, new Task(2, "Brush teeth"));
        repository.addTask(routine, new Task(3, "Jammies"));
        
        // Wait for async operations to complete
        Thread.sleep(1000);
        
        // Simulate "closing and reopening" the app by creating a new repository
        RoutineRepository newRepository = new RoomRoutineRepository(db);
        
        // Use a CountDownLatch to wait for async load
        CountDownLatch latch = new CountDownLatch(1);
        
        // Get routines from new repository
        newRepository.findAll().observe(routines -> {
            if (routines != null && !routines.isEmpty()) {
                latch.countDown();
            }
        });
        
        // Wait for async loading (max 5 seconds)
        assertTrue("Timed out waiting for data to load", latch.await(5, TimeUnit.SECONDS));
        
        // Get the routine
        List<Routine> routines = newRepository.findAll().getValue();
        assertNotNull("Routines should not be null", routines);
        
        // Check if the routine exists
        boolean found = false;
        Routine foundRoutine = null;
        for (Routine r : routines) {
            if ("Friday Evening".equals(r.getName())) {
                found = true;
                foundRoutine = r;
                break;
            }
        }
        
        assertTrue("Routine 'Friday Evening' should exist", found);
        assertEquals("Goal time should be 90 minutes", 90, foundRoutine.getGoalTime());
        
        // Check the tasks
        CountDownLatch taskLatch = new CountDownLatch(1);
        newRepository.findAllMappings().observe(map -> {
            if (map != null && !map.isEmpty()) {
                taskLatch.countDown();
            }
        });
        
        assertTrue("Timed out waiting for tasks to load", taskLatch.await(5, TimeUnit.SECONDS));
        
        List<Task> tasks = newRepository.findAllMappings().getValue().get(foundRoutine);
        assertNotNull("Tasks should not be null", tasks);
        assertEquals("Should have 3 tasks", 3, tasks.size());
        
        // Check task names and order
        assertEquals("First task should be 'Dinner'", "Dinner", tasks.get(0).getName());
        assertEquals("Second task should be 'Brush teeth'", "Brush teeth", tasks.get(1).getName());
        assertEquals("Third task should be 'Jammies'", "Jammies", tasks.get(2).getName());
    }
} 