package edu.ucsd.cse110.habitizer.app;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;

import edu.ucsd.cse110.habitizer.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.lib.domain.RoutineRepository;
import edu.ucsd.cse110.habitizer.lib.domain.SimpleRoutineRepository;

public class GoalTimeTest {
    private InMemoryDataSource dataSource;
    private RoutineRepository repository;

    @Before
    public void setup() {
        dataSource = new InMemoryDataSource();
        repository = new SimpleRoutineRepository(dataSource);
    }

    @Test
    public void testSetGoalTime() {
        Routine routine = new Routine(0, "Test Routine");
        
        routine.setGoalTime(45);
        
        repository.save(routine);
        
        Routine savedRoutine = repository.find(0).getValue();
        
        assertNotNull("Saved routine should not be null", savedRoutine);
        assertEquals("Goal time should be 45 minutes", 45, savedRoutine.getGoalTime());
    }
    
    @Test
    public void testUpdateGoalTime() {
        Routine routine = new Routine(0, "Test Routine");
        
        routine.setGoalTime(30);
        
        repository.save(routine);
        
        routine.setGoalTime(60);
        repository.save(routine);
        
        Routine savedRoutine = repository.find(0).getValue();
        
        assertNotNull("Saved routine should not be null", savedRoutine);
        assertEquals("Goal time should be updated to 60 minutes", 60, savedRoutine.getGoalTime());
    }
    
    @Test
    public void testMultipleRoutinesWithDifferentGoalTimes() {
        Routine routine1 = new Routine(0, "Morning Routine");
        routine1.setGoalTime(30);
        
        Routine routine2 = new Routine(1, "Evening Routine");
        routine2.setGoalTime(45);
        
        Routine routine3 = new Routine(2, "Weekend Routine");
        routine3.setGoalTime(120);
        
        repository.save(routine1);
        repository.save(routine2);
        repository.save(routine3);
        
        List<Routine> routines = repository.findAll().getValue();
        
        assertNotNull("Routines list should not be null", routines);
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
    public void testZeroGoalTime() {
        Routine routine = new Routine(0, "Test Routine");
        
        repository.save(routine);
        
        Routine savedRoutine = repository.find(0).getValue();
        
        assertNotNull("Saved routine should not be null", savedRoutine);
        assertEquals("Default goal time should be 0", 0, savedRoutine.getGoalTime());
    }
} 