package edu.ucsd.cse110.habitizer.lib.domain;

import java.util.List;
import java.util.Map;

import edu.ucsd.cse110.habitizer.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.observables.Subject;
import junit.framework.TestCase;

public class RoutineRepositoryTest extends TestCase {

    public void testFind() {
        InMemoryDataSource dataSource = new InMemoryDataSource();
        Routine routine = new Routine(0, "Morning Routine");
        dataSource.putRoutine(routine);
        RoutineRepository repository = new RoutineRepository(dataSource);

        Subject<Routine> routineSubject = repository.find(0);

        assertNotNull(routineSubject.getValue());
        assertEquals(routine, routineSubject.getValue());

    }

    public void testFindAll() {
        InMemoryDataSource dataSource = new InMemoryDataSource();
        Routine morning = new Routine(0, "Morning Routine");
        Routine evening = new Routine(1, "Evening Routine");
        dataSource.putRoutine(morning);
        dataSource.putRoutine(evening);
        RoutineRepository repository = new RoutineRepository(dataSource);

        Subject<List<Routine>> allRoutinesSubject = repository.findAll();
        List<Routine> currentRoutines = allRoutinesSubject.getValue();

        assertNotNull(currentRoutines);
        assertEquals(2, currentRoutines.size());
        assertTrue(currentRoutines.contains(morning));
        assertTrue(currentRoutines.contains(evening));
    }

    public void testFindAllMappings() {
        InMemoryDataSource dataSource = new InMemoryDataSource();
        Routine routine = new Routine(0, "Morning Routine");
        dataSource.putRoutine(routine);

        Task task = new Task(0, "Wake up");
        dataSource.putTask(routine, task);
        RoutineRepository repository = new RoutineRepository(dataSource);

        Subject<Map<Routine, List<Task>>> mapSubject = repository.findAllMappings();
        Map<Routine, List<Task>> routineTaskMap = mapSubject.getValue();

        assertNotNull(routineTaskMap);
        assertTrue(routineTaskMap.containsKey(routine));
        assertTrue(routineTaskMap.get(routine).contains(task));
    }

    public void testSaveRoutine() {
        InMemoryDataSource dataSource = new InMemoryDataSource();
        RoutineRepository repository = new RoutineRepository(dataSource);
        Routine routine = new Routine(0, "Morning Routine");
        repository.save(routine);

        Routine getRoutineResult = dataSource.getRoutine(0);
        assertNotNull(getRoutineResult);
        assertEquals(routine, getRoutineResult);
    }

    public void testTestSaveRoutineList() {
        InMemoryDataSource dataSource = new InMemoryDataSource();
        RoutineRepository repository = new RoutineRepository(dataSource);
        Routine morning = new Routine(0, "Morning Routine");
        Routine evening = new Routine(1, "Evening Routine");
        repository.save(List.of(morning, evening));

        assertEquals(morning, dataSource.getRoutine(0));
        assertEquals(evening, dataSource.getRoutine(1));
    }

    public void testSwapTaskOrder() {
        InMemoryDataSource dataSource = new InMemoryDataSource();
        RoutineRepository repository = new RoutineRepository(dataSource);
        
        Routine routine = new Routine(1, "Test Routine");
        repository.save(routine);
        
        Task task1 = new Task(101, "First Task");
        Task task2 = new Task(102, "Second Task");
        Task task3 = new Task(103, "Third Task");
        
        dataSource.putTask(routine, task1);
        dataSource.putTask(routine, task2);
        dataSource.putTask(routine, task3);
        
        Map<Routine, List<Task>> initialMapping = repository.findAllMappings().getValue();
        List<Task> initialTasks = initialMapping.get(routine);
        
        assertEquals(3, initialTasks.size());
        assertEquals(Integer.valueOf(101), initialTasks.get(0).getId());
        assertEquals(Integer.valueOf(102), initialTasks.get(1).getId());
        assertEquals(Integer.valueOf(103), initialTasks.get(2).getId());
        
        repository.swapTaskOrder(routine, 0, 2);
        
        Map<Routine, List<Task>> updatedMapping = repository.findAllMappings().getValue();
        List<Task> swappedTasks = updatedMapping.get(routine);
        
        assertEquals(3, swappedTasks.size());
        assertEquals(Integer.valueOf(102), swappedTasks.get(0).getId());
        assertEquals(Integer.valueOf(103), swappedTasks.get(1).getId());
        assertEquals(Integer.valueOf(101), swappedTasks.get(2).getId());
        
        assertEquals("Second Task", swappedTasks.get(0).getName());
        assertEquals("Third Task", swappedTasks.get(1).getName());
        assertEquals("First Task", swappedTasks.get(2).getName());
    }
    
    public void testSwapTaskOrderErrorHandling() {
        InMemoryDataSource dataSource = new InMemoryDataSource();
        RoutineRepository repository = new RoutineRepository(dataSource);
        
        Routine routine = new Routine(1, "Test Routine");
        repository.save(routine);
        
        Task task1 = new Task(101, "First Task");
        Task task2 = new Task(102, "Second Task");
        
        dataSource.putTask(routine, task1);
        dataSource.putTask(routine, task2);
        
        try {
            repository.swapTaskOrder(null, 0, 1);
            fail("Should throw IllegalArgumentException for null routine");
        } catch (IllegalArgumentException e) {
            // Expected
        }
        
        try {
            repository.swapTaskOrder(routine, -1, 1);
        } catch (Exception e) {
            fail("Should handle invalid positions gracefully: " + e.getMessage());
        }
        
        try {
            repository.swapTaskOrder(routine, 0, 5);
        } catch (Exception e) {
            fail("Should handle out of bounds positions gracefully: " + e.getMessage());
        }
        
        Map<Routine, List<Task>> mapping = repository.findAllMappings().getValue();
        List<Task> tasks = mapping.get(routine);
        
        assertEquals(2, tasks.size());
        assertEquals(Integer.valueOf(101), tasks.get(0).getId());
        assertEquals(Integer.valueOf(102), tasks.get(1).getId());
    }
}