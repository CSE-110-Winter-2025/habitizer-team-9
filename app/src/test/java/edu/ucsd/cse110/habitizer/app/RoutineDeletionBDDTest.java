package edu.ucsd.cse110.habitizer.app;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import edu.ucsd.cse110.habitizer.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.lib.domain.RoutineRepository;
import edu.ucsd.cse110.habitizer.lib.domain.SimpleRoutineRepository;
import edu.ucsd.cse110.habitizer.lib.domain.Task;

/**
 * BDD Tests for Story 11: Routine Deletion
 */
public class RoutineDeletionBDDTest {
    private InMemoryDataSource dataSource;
    private RoutineRepository repository;

    @Before
    public void setup() {
        dataSource = new InMemoryDataSource();
        repository = new SimpleRoutineRepository(dataSource);
    }

    /**
     * Scenario 1: Deleting a routine
     * Given I have created multiple routines
     * And at least one routine contains tasks
     * When I delete a routine
     * Then the routine should be removed from the routine list
     * And all associated tasks should also be deleted
     */
    @Test
    public void testDeleteRoutine() {
        Routine routine1 = new Routine(1, "Routine 1");
        Routine routine2 = new Routine(2, "Routine 2");
        Routine routine3 = new Routine(3, "Routine 3");
        
        repository.addRoutine(routine1);
        repository.addRoutine(routine2);
        repository.addRoutine(routine3);
        
        Task task1 = new Task(1, "Task 1 for Routine 2");
        Task task2 = new Task(2, "Task 2 for Routine 2");
        
        repository.addTask(routine2, task1);
        repository.addTask(routine2, task2);
        
        List<Routine> routinesBefore = repository.findAll().getValue();
        assertEquals(3, routinesBefore.size());
        
        Map<Routine, List<Task>> mappingBefore = repository.findAllMappings().getValue();
        List<Task> tasksBefore = mappingBefore.get(routine2);
        assertEquals(2, tasksBefore.size());
        
        repository.deleteRoutine(2);
        
        List<Routine> routinesAfter = repository.findAll().getValue();
        assertEquals(2, routinesAfter.size());
        
        boolean foundRoutine1 = false;
        boolean foundRoutine2 = false;
        boolean foundRoutine3 = false;
        
        for (Routine routine : routinesAfter) {
            if (routine.id() == 1) foundRoutine1 = true;
            if (routine.id() == 2) foundRoutine2 = true;
            if (routine.id() == 3) foundRoutine3 = true;
        }
        
        assertTrue("Routine 1 should still exist", foundRoutine1);
        assertFalse("Routine 2 should be deleted", foundRoutine2);
        assertTrue("Routine 3 should still exist", foundRoutine3);
        
        Map<Routine, List<Task>> mappingAfter = repository.findAllMappings().getValue();
        
        for (Routine routine : mappingAfter.keySet()) {
            assertFalse("Routine 2 should not exist in the mapping", routine.id() == 2);
            
            List<Task> tasks = mappingAfter.get(routine);
            for (Task task : tasks) {
                assertFalse("Tasks from Routine 2 should not exist", 
                    task.getName().contains("for Routine 2"));
            }
        }
    }
    
    /**
     * Scenario 2: Ensuring Routine Deletion Persists After App Restart
     * Given I have deleted a routine
     * When I restart the application
     * Then the deleted routine should not reappear in the routine list
     * And none of its tasks should be restored
     */
    @Test
    public void testRoutineDeletionPersistsAfterRestart() {
        Routine routine1 = new Routine(1, "Routine 1");
        Routine routine2 = new Routine(2, "Routine 2");
        
        repository.addRoutine(routine1);
        repository.addRoutine(routine2);
        
        Task task1 = new Task(1, "Task 1 for Routine 2");
        Task task2 = new Task(2, "Task 2 for Routine 2");
        
        repository.addTask(routine2, task1);
        repository.addTask(routine2, task2);
        
        repository.deleteRoutine(2);
        
        List<Routine> routinesBefore = repository.findAll().getValue();
        assertEquals(1, routinesBefore.size());
        assertTrue("First routine should have ID 1", routinesBefore.get(0).id() == 1);
        
        RoutineRepository newRepository = new SimpleRoutineRepository(dataSource);
        
        List<Routine> routinesAfter = newRepository.findAll().getValue();
        assertEquals(1, routinesAfter.size());
        
        boolean foundRoutine1 = false;
        boolean foundRoutine2 = false;
        
        for (Routine routine : routinesAfter) {
            if (routine.id() == 1) foundRoutine1 = true;
            if (routine.id() == 2) foundRoutine2 = true;
        }
        
        assertTrue("Routine 1 should still exist after restart", foundRoutine1);
        assertFalse("Routine 2 should remain deleted after restart", foundRoutine2);
        
        Map<Routine, List<Task>> mappingAfter = newRepository.findAllMappings().getValue();
        
        for (Routine routine : mappingAfter.keySet()) {
            assertFalse("Routine 2 should not exist in the mapping after restart", routine.id() == 2);
            
            List<Task> tasks = mappingAfter.get(routine);
            for (Task task : tasks) {
                assertFalse("Tasks from Routine 2 should not exist after restart", 
                    task.getName().contains("for Routine 2"));
            }
        }
    }
}