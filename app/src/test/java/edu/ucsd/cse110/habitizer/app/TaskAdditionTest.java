package edu.ucsd.cse110.habitizer.app;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import edu.ucsd.cse110.habitizer.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.lib.domain.RoutineRepository;
import edu.ucsd.cse110.habitizer.lib.domain.SimpleRoutineRepository;
import edu.ucsd.cse110.habitizer.lib.domain.Task;

public class TaskAdditionTest {
    private InMemoryDataSource dataSource;
    private RoutineRepository repository;

    @Before
    public void setup() {
        dataSource = new InMemoryDataSource();
        repository = new SimpleRoutineRepository(dataSource);
    }

    @Test
    public void testAddTaskToNewRoutine() {
        Routine routine1 = new Routine(0, "First Routine");
        Routine routine2 = new Routine(1, "Second Routine");
        Routine routine3 = new Routine(2, "Third Routine");
        Routine routine4 = new Routine(3, "Fourth Routine");
        
        repository.addRoutine(routine1);
        repository.addRoutine(routine2);
        repository.addRoutine(routine3);
        repository.addRoutine(routine4);
        
        Task task1 = new Task(0, "Task 1 for Routine 1");
        Task task2 = new Task(0, "Task 1 for Routine 2");
        Task task3 = new Task(0, "Task 1 for Routine 3");
        Task task4 = new Task(0, "Task 1 for Routine 4");
        
        repository.addTask(routine1, task1);
        repository.addTask(routine2, task2);
        repository.addTask(routine3, task3);
        repository.addTask(routine4, task4);
        
        Map<Routine, List<Task>> routineTaskMap = repository.findAllMappings().getValue();
        
        assertNotNull("Routine task map should not be null", routineTaskMap);
        
        List<Task> tasks1 = routineTaskMap.get(routine1);
        assertNotNull("Tasks for first routine should not be null", tasks1);
        assertEquals("First routine should have 1 task", 1, tasks1.size());
        assertEquals("Task name should match", "Task 1 for Routine 1", tasks1.get(0).getName());
        
        List<Task> tasks2 = routineTaskMap.get(routine2);
        assertNotNull("Tasks for second routine should not be null", tasks2);
        assertEquals("Second routine should have 1 task", 1, tasks2.size());
        assertEquals("Task name should match", "Task 1 for Routine 2", tasks2.get(0).getName());
        
        List<Task> tasks3 = routineTaskMap.get(routine3);
        assertNotNull("Tasks for third routine should not be null", tasks3);
        assertEquals("Third routine should have 1 task", 1, tasks3.size());
        assertEquals("Task name should match", "Task 1 for Routine 3", tasks3.get(0).getName());
        
        List<Task> tasks4 = routineTaskMap.get(routine4);
        assertNotNull("Tasks for fourth routine should not be null", tasks4);
        assertEquals("Fourth routine should have 1 task", 1, tasks4.size());
        assertEquals("Task name should match", "Task 1 for Routine 4", tasks4.get(0).getName());
    }
    
    @Test
    public void testAddMultipleTasksToRoutine() {
        Routine routine = new Routine(0, "Test Routine");
        
        repository.addRoutine(routine);
        
        Task task1 = new Task(0, "First Task");
        Task task2 = new Task(1, "Second Task");
        Task task3 = new Task(2, "Third Task");
        
        repository.addTask(routine, task1);
        repository.addTask(routine, task2);
        repository.addTask(routine, task3);
        
        Map<Routine, List<Task>> routineTaskMap = repository.findAllMappings().getValue();
        
        assertNotNull("Routine task map should not be null", routineTaskMap);
        
        List<Task> tasks = routineTaskMap.get(routine);
        assertNotNull("Tasks should not be null", tasks);
        assertEquals("Routine should have 3 tasks", 3, tasks.size());
        
        assertEquals("First task should be 'First Task'", "First Task", tasks.get(0).getName());
        assertEquals("Second task should be 'Second Task'", "Second Task", tasks.get(1).getName());
        assertEquals("Third task should be 'Third Task'", "Third Task", tasks.get(2).getName());
    }
    
    @Test
    public void testDeleteTask() {
        Routine routine = new Routine(0, "Test Routine");
        repository.addRoutine(routine);
        
        Task task1 = new Task(0, "Task 1");
        Task task2 = new Task(1, "Task 2");
        Task task3 = new Task(2, "Task 3");
        
        repository.addTask(routine, task1);
        repository.addTask(routine, task2);
        repository.addTask(routine, task3);
        
        Map<Routine, List<Task>> beforeDelete = repository.findAllMappings().getValue();
        List<Task> tasksBefore = beforeDelete.get(routine);
        assertEquals(3, tasksBefore.size());
        
        repository.deleteTask(1);
        
        Map<Routine, List<Task>> afterDelete = repository.findAllMappings().getValue();
        List<Task> tasksAfter = afterDelete.get(routine);
        
        assertEquals(2, tasksAfter.size());
        
        boolean found = false;
        for (Task task : tasksAfter) {
            if (task.getId() != null && task.getId() == 1) {
                found = true;
                break;
            }
        }
        
        assertTrue(!found);
    }
    
    @Test
    public void testDeleteMultipleTasks() {
        Routine routine = new Routine(0, "Test Routine");
        repository.addRoutine(routine);
        
        Task task1 = new Task(0, "Task 1");
        Task task2 = new Task(1, "Task 2");
        Task task3 = new Task(2, "Task 3");
        Task task4 = new Task(3, "Task 4");
        Task task5 = new Task(4, "Task 5");
        
        repository.addTask(routine, task1);
        repository.addTask(routine, task2);
        repository.addTask(routine, task3);
        repository.addTask(routine, task4);
        repository.addTask(routine, task5);
        
        Map<Routine, List<Task>> beforeDelete = repository.findAllMappings().getValue();
        List<Task> tasksBefore = beforeDelete.get(routine);
        assertEquals(5, tasksBefore.size());
        
        repository.deleteTask(1);
        repository.deleteTask(3);
        repository.deleteTask(4);
        
        Map<Routine, List<Task>> afterDelete = repository.findAllMappings().getValue();
        List<Task> tasksAfter = afterDelete.get(routine);
        
        assertEquals(2, tasksAfter.size());
        
        boolean found1 = false;
        boolean found2 = false;
        
        for (Task task : tasksAfter) {
            if (task.getId() == 0) found1 = true;
            if (task.getId() == 2) found2 = true;
        }
        
        assertTrue(found1);
        assertTrue(found2);
    }
} 