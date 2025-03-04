package edu.ucsd.cse110.habitizer.lib.domain;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import edu.ucsd.cse110.habitizer.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.observables.Subject;

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

    public void testAddTask() {
        InMemoryDataSource dataSource = new InMemoryDataSource();
        RoutineRepository repository = new RoutineRepository(dataSource);
        Task task = new Task(0, "Wash Face");
        Task expectedTask = new Task(0, "Wash Face");
        Routine routine = new Routine(0, "Morning Routine");
        repository.save(routine);

        repository.addTask(routine, task);
        assertEquals(dataSource.getTasks(routine).size(), 1);
        assertEquals(dataSource.getTasks(routine).get(0), expectedTask);
    }

    public void testAddRoutine() {
        InMemoryDataSource dataSource = new InMemoryDataSource();
        RoutineRepository repository = new RoutineRepository(dataSource);

        Routine routine = new Routine(0, "Morning Routine");
        Integer expectedId = 0;
        String expectedName = "Morning Routine";

        repository.addRoutine(routine);
        assertEquals(dataSource.getRoutines().size(), 1);
        assertEquals(dataSource.getRoutines().get(0).id(), expectedId);
        assertEquals(dataSource.getRoutines().get(0).getName(), expectedName);
    }

    public void testMoveTaskUp(){
        InMemoryDataSource dataSource = new InMemoryDataSource();
        RoutineRepository repository = new RoutineRepository(dataSource);

        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task(0, "Wake Up"));
        tasks.add(new Task(1, "Brush Teeth"));
        tasks.add(new Task(2, "Wash Face"));

        Routine routine = new Routine(0, "Morning Routine");
        repository.save(routine);


        Task expectedTask = new Task(0, "Wash Face");

        repository.addTask(routine, tasks.get(0));
        repository.addTask(routine, tasks.get(1));
        repository.addTask(routine, tasks.get(2));

        assertEquals(new Task(0, "Wake Up"), dataSource.getTasks(routine).get(0));

        repository.moveTaskUp(routine, tasks.get(1));

        assertEquals(new Task(1, "Brush Teeth"), dataSource.getTasks(routine).get(0));
        assertEquals(new Task(0, "Wake Up"), dataSource.getTasks(routine).get(1));
        assertEquals(new Task(2, "Wash Face"), dataSource.getTasks(routine).get(2));

        // tasks.get(1) == new Task(1, "Brush Teeth")
        repository.moveTaskUp(routine, tasks.get(1));

        assertEquals(new Task(1, "Brush Teeth"), dataSource.getTasks(routine).get(2));
        assertEquals(new Task(0, "Wake Up"), dataSource.getTasks(routine).get(1));
        assertEquals(new Task(2, "Wash Face"), dataSource.getTasks(routine).get(0));
    }

    public void testMoveTaskDown(){
        InMemoryDataSource dataSource = new InMemoryDataSource();
        RoutineRepository repository = new RoutineRepository(dataSource);

        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task(0, "Wake Up"));
        tasks.add(new Task(1, "Brush Teeth"));
        tasks.add(new Task(2, "Wash Face"));

        Routine routine = new Routine(0, "Morning Routine");
        repository.save(routine);


        Task expectedTask = new Task(0, "Wash Face");

        repository.addTask(routine, tasks.get(0));
        repository.addTask(routine, tasks.get(1));
        repository.addTask(routine, tasks.get(2));

        assertEquals(new Task(0, "Wake Up"), dataSource.getTasks(routine).get(0));

        repository.moveTaskDown(routine, tasks.get(1));

        assertEquals(new Task(1, "Brush Teeth"), dataSource.getTasks(routine).get(2));
        assertEquals(new Task(0, "Wake Up"), dataSource.getTasks(routine).get(0));
        assertEquals(new Task(2, "Wash Face"), dataSource.getTasks(routine).get(1));

        // tasks.get(1) == new Task(1, "Brush Teeth")
        repository.moveTaskDown(routine, tasks.get(1));


        assertEquals(new Task(1, "Brush Teeth"), dataSource.getTasks(routine).get(0));
        assertEquals(new Task(0, "Wake Up"), dataSource.getTasks(routine).get(2));
        assertEquals(new Task(2, "Wash Face"), dataSource.getTasks(routine).get(1));
    }
}