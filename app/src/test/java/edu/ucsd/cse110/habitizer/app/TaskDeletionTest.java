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

/**
 * BDD Tests for Story 5: Task Deletion
 */
public class TaskDeletionTest {
    private InMemoryDataSource dataSource;
    private RoutineRepository repository;

    @Before
    public void setup() {
        dataSource = new InMemoryDataSource();
        repository = new SimpleRoutineRepository(dataSource);
    }

    /**
     * Scenario 1: Deleting a task from a routine
     * Given I have created a routine with multiple tasks
     * And the task list contains at least one task
     * When I delete a specific task from the routine
     * Then the task should be removed from the task list
     * And the remaining tasks should still be visible
     */
    @Test
    public void testDeleteTaskFromRoutine() {
        Routine routine = new Routine(1, "Test Routine");
        repository.addRoutine(routine);
        
        Task task1 = new Task(1, "Task 1");
        Task task2 = new Task(2, "Task 2");
        Task task3 = new Task(3, "Task 3");
        
        repository.addTask(routine, task1);
        repository.addTask(routine, task2);
        repository.addTask(routine, task3);
        
        Map<Routine, List<Task>> mappingBefore = repository.findAllMappings().getValue();
        assertNotNull(mappingBefore.get(routine));
        assertEquals(3, mappingBefore.get(routine).size());
        
        repository.deleteTask(2);
        
        Map<Routine, List<Task>> mappingAfter = repository.findAllMappings().getValue();
        List<Task> tasksAfter = mappingAfter.get(routine);
        
        assertEquals(2, tasksAfter.size());
        
        boolean foundTask1 = false;
        boolean foundTask2 = false;
        boolean foundTask3 = false;
        
        for (Task task : tasksAfter) {
            if (task.getId() != null && task.getId() == 1) foundTask1 = true;
            if (task.getId() != null && task.getId() == 2) foundTask2 = true;
            if (task.getId() != null && task.getId() == 3) foundTask3 = true;
        }
        
        assertTrue("Task 1 should still exist", foundTask1);
        assertFalse("Task 2 should be deleted", foundTask2);
        assertTrue("Task 3 should still exist", foundTask3);
    }
    
    /**
     * Scenario 2: Deleting the last task in a routine
     * Given I have a routine with only one task
     * When I delete the task
     * Then the routine should remain visible
     * And the routine should display an empty task list
     */
    @Test
    public void testDeleteLastTaskInRoutine() {
        Routine routine = new Routine(1, "Test Routine");
        repository.addRoutine(routine);
        
        Task task = new Task(1, "Only Task");
        repository.addTask(routine, task);
        
        Map<Routine, List<Task>> mappingBefore = repository.findAllMappings().getValue();
        assertNotNull(mappingBefore.get(routine));
        assertEquals(1, mappingBefore.get(routine).size());
        
        repository.deleteTask(1);
        
        List<Routine> routinesAfter = repository.findAll().getValue();
        assertEquals(1, routinesAfter.size());
        
        boolean routineFound = false;
        for (Routine r : routinesAfter) {
            if (r.id() == 1) {
                routineFound = true;
                break;
            }
        }
        assertTrue("Routine should still exist", routineFound);
        
        Map<Routine, List<Task>> mappingAfter = repository.findAllMappings().getValue();
        List<Task> tasksAfter = mappingAfter.get(routine);
        
        assertTrue("Task list should be empty", tasksAfter == null || tasksAfter.isEmpty());
    }
    
    /**
     * Scenario 3: Ensuring Task Deletion Persists After App Restart
     * Given I have deleted a task from a routine
     * When I restart the application
     * Then the deleted task should not reappear in the task list
     */
    @Test
    public void testTaskDeletionPersistsAfterRestart() {
        Routine routine = new Routine(1, "Test Routine");
        repository.addRoutine(routine);
        
        Task task1 = new Task(1, "Task 1");
        Task task2 = new Task(2, "Task 2");
        
        repository.addTask(routine, task1);
        repository.addTask(routine, task2);
        
        repository.deleteTask(2);
        
        Map<Routine, List<Task>> mappingBefore = repository.findAllMappings().getValue();
        List<Task> tasksBefore = mappingBefore.get(routine);
        assertEquals(1, tasksBefore.size());
        assertEquals(Integer.valueOf(1), tasksBefore.get(0).getId());
        
        RoutineRepository newRepository = new SimpleRoutineRepository(dataSource);
        
        Map<Routine, List<Task>> mappingAfter = newRepository.findAllMappings().getValue();
        
        Routine retrievedRoutine = null;
        for (Routine r : mappingAfter.keySet()) {
            if (r.id() == 1) {
                retrievedRoutine = r;
                break;
            }
        }
        
        assertNotNull("Routine should exist after restart", retrievedRoutine);
        
        List<Task> tasksAfter = mappingAfter.get(retrievedRoutine);
        assertEquals(1, tasksAfter.size());
        
        boolean foundTask1 = false;
        boolean foundTask2 = false;
        
        for (Task task : tasksAfter) {
            if (task.getId() != null && task.getId() == 1) foundTask1 = true;
            if (task.getId() != null && task.getId() == 2) foundTask2 = true;
        }
        
        assertTrue("Task 1 should still exist after restart", foundTask1);
        assertFalse("Task 2 should remain deleted after restart", foundTask2);
    }
} 