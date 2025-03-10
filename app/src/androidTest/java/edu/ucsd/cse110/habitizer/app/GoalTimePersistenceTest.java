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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class GoalTimePersistenceTest {
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
    public void testGoalTimePersistence() throws InterruptedException {
        Routine routine1 = new Routine(1, "Morning Routine");
        routine1.setGoalTime(30);
        
        Routine routine2 = new Routine(2, "Evening Routine");
        routine2.setGoalTime(45);
        
        Routine routine3 = new Routine(3, "Weekend Routine");
        routine3.setGoalTime(120);
        
        repository.save(routine1);
        repository.save(routine2);
        repository.save(routine3);
        
        Thread.sleep(1000);
        
        RoutineRepository newRepository = new RoomRoutineRepository(db);
        
        CountDownLatch latch = new CountDownLatch(1);
        
        newRepository.findAll().observe(routines -> {
            if (routines != null && routines.size() >= 3) {
                latch.countDown();
            }
        });
        
        assertTrue("Timed out waiting for data to load", latch.await(5, TimeUnit.SECONDS));
        
        List<Routine> routines = newRepository.findAll().getValue();
        assertNotNull("Routines should not be null", routines);
        assertEquals("Should have 3 routines", 3, routines.size());
        
        for (Routine routine : routines) {
            if (routine.getName().equals("Morning Routine")) {
                assertEquals("Morning Routine should have goal time of 30 minutes", 30, routine.getGoalTime());
            } else if (routine.getName().equals("Evening Routine")) {
                assertEquals("Evening Routine should have goal time of 45 minutes", 45, routine.getGoalTime());
            } else if (routine.getName().equals("Weekend Routine")) {
                assertEquals("Weekend Routine should have goal time of 120 minutes", 120, routine.getGoalTime());
            }
        }
    }
    
    @Test
    public void testUpdateGoalTime() throws InterruptedException {
        Routine routine = new Routine(1, "Study Routine");
        routine.setGoalTime(60);
        
        repository.save(routine);
        
        Thread.sleep(1000);
        
        routine.setGoalTime(90);
        repository.save(routine);
        
        Thread.sleep(1000);
        
        RoutineRepository newRepository = new RoomRoutineRepository(db);
        
        CountDownLatch latch = new CountDownLatch(1);
        
        newRepository.findAll().observe(routines -> {
            if (routines != null && !routines.isEmpty()) {
                latch.countDown();
            }
        });
        
        assertTrue("Timed out waiting for data to load", latch.await(5, TimeUnit.SECONDS));
        
        List<Routine> routines = newRepository.findAll().getValue();
        assertNotNull("Routines should not be null", routines);
        
        boolean found = false;
        for (Routine r : routines) {
            if (r.getName().equals("Study Routine")) {
                found = true;
                assertEquals("Study Routine should have updated goal time of 90 minutes", 90, r.getGoalTime());
                break;
            }
        }
        
        assertTrue("Study Routine should exist", found);
    }
} 