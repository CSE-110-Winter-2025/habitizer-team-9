package edu.ucsd.cse110.habitizer.app;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import edu.ucsd.cse110.habitizer.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.lib.domain.RoutineRepository;
import edu.ucsd.cse110.habitizer.lib.domain.SimpleRoutineRepository;
import edu.ucsd.cse110.habitizer.lib.domain.Task;

public class RoutineDeleteTest {
    private InMemoryDataSource dataSource;
    private RoutineRepository repository;

    @Before
    public void setup() {
        dataSource = new InMemoryDataSource();
        repository = new SimpleRoutineRepository(dataSource);
    }

    @Test
    public void testDeleteRoutine() {
        Routine routine1 = new Routine(0, "First Routine");
        Routine routine2 = new Routine(1, "Second Routine");
        Routine routine3 = new Routine(2, "Third Routine");
        
        repository.addRoutine(routine1);
        repository.addRoutine(routine2);
        repository.addRoutine(routine3);
        
        List<Routine> routinesBefore = repository.findAll().getValue();
        assertEquals(3, routinesBefore.size());
        
        repository.deleteRoutine(1);
        
        List<Routine> routinesAfter = repository.findAll().getValue();
        assertEquals(2, routinesAfter.size());
        
        boolean found = false;
        for (Routine routine : routinesAfter) {
            if (routine.id() == 1) {
                found = true;
                break;
            }
        }
        
        assertFalse(found);
    }
    
    @Test
    public void testDeleteRoutineWithTasks() {
        Routine routine = new Routine(0, "Test Routine");
        repository.addRoutine(routine);
        
        Task task1 = new Task(0, "Task 1");
        Task task2 = new Task(1, "Task 2");
        Task task3 = new Task(2, "Task 3");
        
        repository.addTask(routine, task1);
        repository.addTask(routine, task2);
        repository.addTask(routine, task3);
        
        Map<Routine, List<Task>> mappingBefore = repository.findAllMappings().getValue();
        assertNotNull(mappingBefore.get(routine));
        assertEquals(3, mappingBefore.get(routine).size());
        
        repository.deleteRoutine(0);
        
        List<Routine> routinesAfter = repository.findAll().getValue();
        assertEquals(0, routinesAfter.size());
        
        Map<Routine, List<Task>> mappingAfter = repository.findAllMappings().getValue();
        assertTrue(mappingAfter.isEmpty() || !mappingAfter.containsKey(routine));
    }
    
    @Test
    public void testDeleteMultipleRoutines() {
        Routine routine1 = new Routine(0, "First Routine");
        Routine routine2 = new Routine(1, "Second Routine");
        Routine routine3 = new Routine(2, "Third Routine");
        Routine routine4 = new Routine(3, "Fourth Routine");
        Routine routine5 = new Routine(4, "Fifth Routine");
        
        repository.addRoutine(routine1);
        repository.addRoutine(routine2);
        repository.addRoutine(routine3);
        repository.addRoutine(routine4);
        repository.addRoutine(routine5);
        
        List<Routine> routinesBefore = repository.findAll().getValue();
        assertEquals(5, routinesBefore.size());
        
        repository.deleteRoutine(0);
        repository.deleteRoutine(2);
        repository.deleteRoutine(4);
        
        List<Routine> routinesAfter = repository.findAll().getValue();
        assertEquals(2, routinesAfter.size());
        
        boolean found1 = false;
        boolean found2 = false;
        
        for (Routine routine : routinesAfter) {
            if (routine.id() == 1) found1 = true;
            if (routine.id() == 3) found2 = true;
        }
        
        assertTrue(found1);
        assertTrue(found2);
    }
    
    @Test
    public void testRoutineOrderAfterDeletion() {
        Routine routine1 = new Routine(0, "First Routine");
        Routine routine2 = new Routine(1, "Second Routine");
        Routine routine3 = new Routine(2, "Third Routine");
        
        repository.addRoutine(routine1);
        repository.addRoutine(routine2);
        repository.addRoutine(routine3);
        
        repository.deleteRoutine(1);
        
        Routine routine4 = new Routine(3, "Fourth Routine");
        repository.addRoutine(routine4);
        
        List<Routine> routines = repository.findAll().getValue();
        assertEquals(3, routines.size());
        
        boolean found0 = false;
        boolean found2 = false;
        boolean found3 = false;
        
        for (Routine routine : routines) {
            if (routine.id() == 0) found0 = true;
            if (routine.id() == 2) found2 = true;
            if (routine.id() == 3) found3 = true;
        }
        
        assertTrue(found0);
        assertTrue(found2);
        assertTrue(found3);
    }
} 